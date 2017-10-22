package com.devskiller.hbm2ddl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

class SchemaGenerator {

	private static final String DB_URL = "jdbc:h2:mem:hbm2ddl";

	void generate(GeneratorSettings settings) throws Exception {
		if (settings.getJpaProperties().getProperty("hibernate.dialect") == null) {
			settings.getJpaProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		}

		File outputFile = settings.getOutputPath();
		if (settings.getGenerationMode() == GenerationMode.DATABASE) {
			settings.getJpaProperties().setProperty("hibernate.connection.url", DB_URL);
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
				List<Path> resolvedMigrations = FileResolver.resolveExistingMigrations(settings.getOutputPath(), false);
				for (Path resolvedMigration : resolvedMigrations) {
					connection = DriverManager.getConnection(DB_URL, "SA", "");
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
			List<String> lines = Files.readAllLines(outputFile.toPath())
					.stream()
					.map(line -> line.replaceAll("HBM2DDL\\.", ""))
					.collect(Collectors.toList());
			Files.write(outputFile.toPath(), lines);
		}
	}

}