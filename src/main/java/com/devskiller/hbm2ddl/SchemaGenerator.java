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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

class SchemaGenerator {

	void generate(File outputFile, List<String> packages, SchemaExport.Action action, Properties jpaProperties, boolean formatOutput, String delimiter) throws Exception {
		Path schemaPath = outputFile.toPath();
		Files.deleteIfExists(schemaPath);
		if (jpaProperties.getProperty("hibernate.dialect") == null) {
			jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		}
		MetadataSources metadata = new MetadataSources(
				new StandardServiceRegistryBuilder()
						.applySettings(jpaProperties)
						.build());

		for (String packageName : packages) {
			listClassNamesInPackage(packageName).forEach(metadata::addAnnotatedClassName);
		}

		SchemaExport export = new SchemaExport();
		export.setFormat(formatOutput);
		export.setDelimiter(delimiter);
		export.setOutputFile(schemaPath.toAbsolutePath().toString());
		export.execute(EnumSet.of(TargetType.SCRIPT), action, metadata.buildMetadata());

		// https://github.com/jOOQ/jOOQ/issues/6707
		if (schemaPath.toFile().exists()) {
			byte[] bytes = Files.readAllBytes(schemaPath);
			if (bytes.length > 0 && bytes[bytes.length - 1] == '\n') {
				Files.write(schemaPath, Arrays.copyOf(bytes, bytes.length - 1));
			}
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