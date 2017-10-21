package com.devskiller.hbm2ddl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

class SchemaGenerator {

	private static final String DB_URL = "jdbc:h2:mem:hbm2ddl";

	void generate(GeneratorSettings settings) throws Exception {
		Path schemaPath = settings.getSchemaFile().toPath();
		if (settings.getAction() != Action.UPDATE) {
			Files.deleteIfExists(schemaPath);
		}
		if (settings.getJpaProperties().getProperty("hibernate.dialect") == null) {
			settings.getJpaProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		}

		File outputFile = settings.getSchemaFile();
		if (settings.getGenerationMode() == GenerationMode.DATABASE) {
			settings.getJpaProperties().setProperty("hibernate.connection.url", DB_URL);
			settings.getJpaProperties().setProperty("hibernate.connection.username", "sa");
			settings.getJpaProperties().setProperty("hibernate.connection.password", "");
			settings.getJpaProperties().setProperty("javax.persistence.schema-generation.scripts.action", settings.getAction().toSchemaGenerationAction());

			if (settings.getAction() == Action.UPDATE) {
				outputFile = FileResolver.resolveNextMigrationFile(settings.getMigrationsDir());
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
			listClassNamesInPackage(packageName).forEach(metadata::addAnnotatedClassName);
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
				if (schemaPath.toFile().exists()) {
					connection = DriverManager.getConnection(DB_URL, "SA", "");
					String statement = new String(Files.readAllBytes(schemaPath));
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

	private static List<String> listClassNamesInPackage(String packageName) throws Exception {
		List<String> classes = new ArrayList<>();
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', File.separatorChar));
		if (!resources.hasMoreElements()) {
			throw new IllegalStateException("No package found: " + packageName);
		}
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.class");
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			Files.walkFileTree(Paths.get(resource.getPath()), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					if (pathMatcher.matches(path.getFileName())) {
						String className = Paths.get(resource.getPath()).relativize(path).toString().replace(File.separatorChar, '.');
						classes.add(packageName + '.' + className.substring(0, className.length() - 6));
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
		return classes;
	}

}