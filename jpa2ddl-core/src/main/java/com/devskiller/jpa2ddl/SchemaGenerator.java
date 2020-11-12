package com.devskiller.jpa2ddl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.querydsl.sql.codegen.MetaDataExporter;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import com.devskiller.jpa2ddl.engines.EngineDecorator;

class SchemaGenerator {

	private static final String DB_URL = "jdbc:h2:mem:jpa2ddl";
	private static final String HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String HIBERNATE_SCHEMA_FILTER_PROVIDER = "hibernate.hbm2ddl.schema_filter_provider";

	void generate(GeneratorSettings settings) throws Exception {
		validateSettings(settings);

		if (settings.getJpaProperties().getProperty(HIBERNATE_DIALECT) == null) {
			settings.getJpaProperties().setProperty(HIBERNATE_DIALECT, "org.hibernate.dialect.H2Dialect");
		}

		if (settings.isSkipSequences() && settings.getJpaProperties().getProperty(HIBERNATE_SCHEMA_FILTER_PROVIDER) == null) {
			settings.getJpaProperties().setProperty(HIBERNATE_SCHEMA_FILTER_PROVIDER, NoSequenceFilterProvider.class.getCanonicalName());
		}

		File outputFile = settings.getOutputPath();

		EngineDecorator engineDecorator = EngineDecorator.getEngineDecorator(settings.getJpaProperties().getProperty(HIBERNATE_DIALECT));

		if (settings.getGenerationMode() == GenerationMode.DATABASE) {

			if (settings.getAction() == Action.UPDATE) {
				outputFile = FileResolver.resolveNextMigrationFile(settings.getOutputPath());
			}

			settings.getJpaProperties().setProperty("hibernate.connection.url", getDbUrl(engineDecorator));
			settings.getJpaProperties().setProperty("hibernate.connection.username", "sa");
			settings.getJpaProperties().setProperty("hibernate.connection.password", "");
			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.action", settings.getAction().toSchemaGenerationAction());
			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.database.action", settings.getAction().toSchemaGenerationAction());

			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.create-target", outputFile.getAbsolutePath());
			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.drop-target", outputFile.getAbsolutePath());

			settings.getJpaProperties().setProperty("hibernate.hbm2ddl.delimiter", settings.getDelimiter());
			settings.getJpaProperties().setProperty("hibernate.format_sql", String.valueOf(settings.isFormatOutput()));
		}

		MetadataSources metadata = new MetadataSources(
				new StandardServiceRegistryBuilder()
						.applySettings(settings.getJpaProperties())
						.build());

		for (String packageName: settings.getPackages().stream().sorted().collect(Collectors.toList())) {
			FileResolver.listClassNamesInPackage(packageName).stream().sorted().forEach(metadata::addAnnotatedClassName);
			metadata.addPackage(packageName);
		}

		if (settings.getAction() != Action.UPDATE) {
			Files.deleteIfExists(settings.getOutputPath().toPath());
		}

		if (settings.getGenerationMode() == GenerationMode.METADATA) {
			SchemaExport export = new SchemaExport();
			export.setFormat(settings.isFormatOutput());
			export.setDelimiter(settings.getDelimiter());
			export.setOutputFile(outputFile.getAbsolutePath());
			export.execute(EnumSet.of(TargetType.SCRIPT), settings.getAction().toSchemaExportAction(), metadata.buildMetadata());
		} else {
			Connection connection = null;
			if (settings.getAction() == Action.UPDATE) {
				connection = DriverManager.getConnection(getDbUrl(engineDecorator), "SA", "");

				engineDecorator.decorateDatabaseInitialization(connection);

				List<Path> resolvedMigrations = FileResolver.resolveExistingMigrations(settings.getOutputPath(), false, true);
				for (Path resolvedMigration : resolvedMigrations) {
					String statement = new String(Files.readAllBytes(resolvedMigration));
					connection.prepareStatement(statement).execute();
				}
			}

			metadata.buildMetadata().buildSessionFactory().close();

			if (connection != null) {
				DatabaseMetaData metaData = connection.getMetaData();

				MetaDataExporter metaDataExporter = new MetaDataExporter();
				metaDataExporter.setTargetFolder(settings.getQueryDslOutputPath());
				if (settings.getQueryDslOutputPackage() != null) {
					metaDataExporter.setPackageName(settings.getQueryDslOutputPackage());
				}
				metaDataExporter.export(metaData);
			} else {
				// support other activies than update - bootsrap database manually
			}

			if (connection != null) {
				connection.close();
			}
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

	private String getDbUrl(EngineDecorator engineDecorator) {
		return engineDecorator.decorateConnectionString(DB_URL);
	}

	private void validateSettings(GeneratorSettings settings) {
		if (settings.getAction() == Action.UPDATE) {
			if (settings.getOutputPath().exists() && !settings.getOutputPath().isDirectory()) {
				throw new IllegalArgumentException("For UPDATE action outputPath must be a directory");
			}
			if (settings.getGenerationMode() != GenerationMode.DATABASE) {
				throw new IllegalArgumentException("For UPDATE action generation mode must be set to DATABASE");
			}
		}
	}

}
