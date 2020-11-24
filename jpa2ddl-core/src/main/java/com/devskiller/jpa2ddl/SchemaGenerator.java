package com.devskiller.jpa2ddl;

import com.devskiller.jpa2ddl.engines.EngineDecorator;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class SchemaGenerator {

	private static final String DB_URL = "jdbc:h2:mem:jpa2ddl";
	private static final String HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String HIBERNATE_SCHEMA_FILTER_PROVIDER = "hibernate.hbm2ddl.schema_filter_provider";

	private final ClassResolver classResolver = new ClassResolver();
	private final DatabaseSchemaProcessor databaseSchemaProcessor = new DatabaseSchemaProcessor(classResolver);

	void generate(GeneratorSettings settings) throws Exception {
		validateSettings(settings);

		if (settings.getJpaProperties().getProperty(HIBERNATE_DIALECT) == null) {
			settings.getJpaProperties().setProperty(HIBERNATE_DIALECT, "org.hibernate.dialect.H2Dialect");
		}

		if (settings.isSkipSequences() && settings.getJpaProperties().getProperty(HIBERNATE_SCHEMA_FILTER_PROVIDER) == null) {
			settings.getJpaProperties().setProperty(HIBERNATE_SCHEMA_FILTER_PROVIDER, NoSequenceFilterProvider.class.getCanonicalName());
		}

		File outputFile = settings.getOutputPath();

		if (settings.getAction() == Action.UPDATE) {
			outputFile = FileResolver.resolveNextMigrationFile(settings.getOutputPath());
		} else {
			Files.deleteIfExists(settings.getOutputPath().toPath());
		}

		if (settings.getGenerationMode() == GenerationMode.METADATA) {
			handleMetadataGeneration(settings, outputFile);
		} else if (settings.getGenerationMode() == GenerationMode.EMBEDDED_DATABASE) {
			handleEmbeddedDatabaseGeneration(settings, outputFile);
		} else if (settings.getGenerationMode() == GenerationMode.CONTAINER_DATABASE) {
			handleContainerDatabaseGeneration(settings, outputFile);
		}

		if (outputFile.exists()) {
			if (outputFile.length() == 0) {
				Files.delete(outputFile.toPath());
			} else {
				List<String> lines = Files.readAllLines(outputFile.toPath())
						.stream()
						.map(line -> line.replaceAll("(?i)JPA2DDL\\.(PUBLIC\\.)?", ""))
						.collect(Collectors.toList());
				Files.write(outputFile.toPath(), lines);
			}
		}
	}

	private void handleContainerDatabaseGeneration(GeneratorSettings settings, File outputFile) throws Exception {
		Class<? extends JdbcDatabaseContainer> jdbcContainerClass = resolveJdbcContainerClass();

		JdbcDatabaseContainer jdbcDatabaseContainer = jdbcContainerClass.getDeclaredConstructor().newInstance();
		String driverClassName = jdbcDatabaseContainer.getDriverClassName();

		try {
			jdbcDatabaseContainer.start();

			databaseSchemaProcessor.processDatabase(settings, outputFile, jdbcDatabaseContainer.getJdbcUrl(), driverClassName, jdbcDatabaseContainer.getUsername(), jdbcDatabaseContainer.getPassword(), null);
		} finally {
			if (jdbcDatabaseContainer.isRunning()) {
				jdbcDatabaseContainer.stop();
			}
		}
	}

	private void handleEmbeddedDatabaseGeneration(GeneratorSettings settings, File outputFile) throws Exception {
		EngineDecorator engineDecorator = EngineDecorator.getEngineDecorator(settings.getJpaProperties().getProperty(HIBERNATE_DIALECT));

		String dbUrl = engineDecorator.decorateConnectionString(DB_URL);

		databaseSchemaProcessor.processDatabase(settings, outputFile, dbUrl, "org.h2.Driver", "sa", "", engineDecorator::decorateDatabaseInitialization);
	}

	private void handleMetadataGeneration(GeneratorSettings settings, File outputFile) throws Exception {
		MetadataSources metadata = databaseSchemaProcessor.prepareMetadataSources(settings, outputFile, null, null, null);

		SchemaExport export = new SchemaExport();
		export.setFormat(settings.isFormatOutput());
		export.setDelimiter(settings.getDelimiter());
		export.setOutputFile(outputFile.getAbsolutePath());
		export.execute(EnumSet.of(TargetType.SCRIPT), settings.getAction().toSchemaExportAction(), metadata.buildMetadata());
	}

	private Class<? extends JdbcDatabaseContainer> resolveJdbcContainerClass() {
		Set<Class<? extends JdbcDatabaseContainer>> subTypesOf = classResolver.getSubTypesOf(JdbcDatabaseContainer.class);
		if (subTypesOf.size() > 1) {
			throw new IllegalStateException("More than one JdbcDatabaseContainer found");
		} else if (subTypesOf.isEmpty()) {
			throw new IllegalStateException("No JdbcDatabaseContainer found - please add dependency to proper test-containers module");
		}

		return subTypesOf.iterator().next();
	}

	private void validateSettings(GeneratorSettings settings) {
		if (settings.getAction() == Action.UPDATE) {
			if (settings.getOutputPath().exists() && !settings.getOutputPath().isDirectory()) {
				throw new IllegalArgumentException("For UPDATE action outputPath must be a directory");
			}
			if (settings.getGenerationMode() != GenerationMode.EMBEDDED_DATABASE && settings.getGenerationMode() != GenerationMode.CONTAINER_DATABASE) {
				throw new IllegalArgumentException("For UPDATE action generation mode must be set to DATABASE");
			}
		}
	}

}
