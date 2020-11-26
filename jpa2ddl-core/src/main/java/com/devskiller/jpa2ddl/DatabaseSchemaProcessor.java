package com.devskiller.jpa2ddl;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class DatabaseSchemaProcessor {

	private ClassResolver classResolver;

	DatabaseSchemaProcessor(ClassResolver classResolver) {
		this.classResolver = classResolver;
	}

	void processDatabase(GeneratorSettings settings, File outputFile, String jdbcUrl, String driverClassName, String username, String password, Consumer<Connection> connectionPostProcessor) throws Exception {
		Class.forName(driverClassName); // force driver class load

		try(Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
			if (connectionPostProcessor != null) {
				connectionPostProcessor.accept(connection);
			}
			applyExistingMigrations(settings, connection);
			MetadataSources metadata = prepareMetadataSources(settings, outputFile, jdbcUrl, username, password);
			metadata.buildMetadata().buildSessionFactory().close();
			executePostProcessors(settings, connection);
		}
	}


	MetadataSources prepareMetadataSources(GeneratorSettings settings, File outputFile, String dbUrl, String username, String password) throws Exception {
		if (settings.getGenerationMode() == GenerationMode.EMBEDDED_DATABASE || settings.getGenerationMode() == GenerationMode.CONTAINER_DATABASE) {
			settings.getJpaProperties().setProperty("hibernate.connection.url", dbUrl);
			settings.getJpaProperties().setProperty("hibernate.connection.username", username);
			settings.getJpaProperties().setProperty("hibernate.connection.password", password);
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
		return metadata;
	}

	private void applyExistingMigrations(GeneratorSettings settings, Connection connection) throws IOException, SQLException {
		if (settings.getAction() == Action.UPDATE) {
			List<Path> resolvedMigrations = FileResolver.resolveExistingMigrations(settings.getOutputPath(), false, true);
			for (Path resolvedMigration : resolvedMigrations) {
				String statement = new String(Files.readAllBytes(resolvedMigration));
				connection.prepareStatement(statement).execute();
			}
		}
	}

	private void executePostProcessors(GeneratorSettings settings, Connection connection) throws Exception {
		Set<Class<? extends SchemaProcessor>> schemaProcessorClasses = classResolver.getSubTypesOf(SchemaProcessor.class);
		for (Class<? extends SchemaProcessor> schemaProcessorClass : schemaProcessorClasses) {
			SchemaProcessor schemaProcessor = schemaProcessorClass.getDeclaredConstructor().newInstance();
			schemaProcessor.postProcess(connection, settings.getProcessorProperties());
		}
	}
}
