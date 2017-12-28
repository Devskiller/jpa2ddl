package com.devskiller.jpa2ddl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.h2.util.Utils;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

class SchemaGenerator {

	public static final String POSTGRESQL_MODE = "PostgreSQL";
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

		Optional<String> dbMode = discoverDatabaseMode(settings.getJpaProperties().getProperty(HIBERNATE_DIALECT));

		if (settings.getGenerationMode() == GenerationMode.DATABASE) {


			String dbUrl = DB_URL +
					dbMode
							.map(mode -> ";MODE=" + mode)
							.orElse("") +
					dbMode
							.filter(mode -> mode.equals(POSTGRESQL_MODE))
							.map(mode -> ";INIT=set search_path to pg_catalog,public;")
							.orElse("");


			settings.getJpaProperties().setProperty("hibernate.connection.url", dbUrl);
			settings.getJpaProperties().setProperty("hibernate.connection.username", "sa");
			settings.getJpaProperties().setProperty("hibernate.connection.password", "");
			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.action", settings.getAction().toSchemaGenerationAction());

			if (settings.getAction() == Action.UPDATE) {
				outputFile = FileResolver.resolveNextMigrationFile(settings.getOutputPath());
			}

			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.create-target", outputFile.getAbsolutePath());
			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.drop-target", outputFile.getAbsolutePath());

			settings.getJpaProperties().setProperty("hibernate.hbm2ddl.delimiter", settings.getDelimiter());
			settings.getJpaProperties().setProperty("hibernate.format_sql", String.valueOf(settings.isFormatOutput()));
		}

		MetadataSources metadata = new MetadataSources(
				new StandardServiceRegistryBuilder()
						.applySettings(settings.getJpaProperties())
						.build());

		for (String packageName : settings.getPackages()) {
			FileResolver.listClassNamesInPackage(packageName).forEach(metadata::addAnnotatedClassName);
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
				connection = DriverManager.getConnection(DB_URL, "SA", "");

				if (dbMode.filter(mode -> mode.equals(POSTGRESQL_MODE)).isPresent()) {
					String dbInit = new String(Utils.getResource("/org/h2/server/pg/pg_catalog.sql"));
					connection.prepareStatement(dbInit).execute();
				}

				List<Path> resolvedMigrations = FileResolver.resolveExistingMigrations(settings.getOutputPath(), false);
				for (Path resolvedMigration : resolvedMigrations) {
					String statement = new String(Files.readAllBytes(resolvedMigration));
					connection.prepareStatement(statement).execute();
				}
			}

			metadata.buildMetadata().buildSessionFactory().close();

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

	private Optional<String> discoverDatabaseMode(String dialect) throws ClassNotFoundException {
		Class<?> dialectClass = Class.forName(dialect);
		if (MySQLDialect.class.isAssignableFrom(dialectClass)) {
			return Optional.of("MYSQL");
		} else if (PostgreSQL81Dialect.class.isAssignableFrom(dialectClass)) {
			return Optional.of(POSTGRESQL_MODE);
		} else if (Oracle8iDialect.class.isAssignableFrom(dialectClass)) {
			return Optional.of("Oracle");
		} else if (SQLServerDialect.class.isAssignableFrom(dialectClass)) {
			return Optional.of("MSSQLServer");
		}
		return Optional.empty();
	}

}