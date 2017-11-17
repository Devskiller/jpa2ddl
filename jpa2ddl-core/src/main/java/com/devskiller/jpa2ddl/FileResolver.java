package com.devskiller.jpa2ddl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class FileResolver {

	private final static Pattern FILENAME_PATTERN = Pattern.compile("v([0-9]+)__.+\\.sql");

	static File resolveNextMigrationFile(File migrationDir) {
		Optional<Path> lastFile = resolveExistingMigrations(migrationDir, true)
				.stream()
				.findFirst();

		Integer fileIndex = lastFile.map((Path input) -> FILENAME_PATTERN.matcher(input.getFileName().toString()))
				.map(matcher -> {
					if (matcher.find()) {
						return Integer.valueOf(matcher.group(1));
					} else {
						return 0;
					}
				}).orElse(0);

		return migrationDir.toPath().resolve("v" + ++fileIndex + "__jpa2ddl.sql").toFile();
	}

	static List<Path> resolveExistingMigrations(File migrationsDir, boolean reversed) {
		if (!migrationsDir.exists()) {
			migrationsDir.mkdirs();
		}

		File[] files = migrationsDir.listFiles();

		if (files == null) {
			return Collections.emptyList();
		}

		Comparator<Path> pathComparator = Comparator.comparingInt(FileResolver::compareVersionedMigrations);
		if (reversed) {
			pathComparator = pathComparator.reversed();
		}
		return Arrays.stream(files)
				.map(File::toPath)
				.sorted(pathComparator)
				.collect(Collectors.toList());
	}

	static List<String> listClassNamesInPackage(String packageName) throws Exception {
		List<String> classes = new ArrayList<>();
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', File.separatorChar));
		if (!resources.hasMoreElements()) {
			throw new IllegalStateException("No package found: " + packageName);
		}
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.class");
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			Files.walkFileTree(Paths.get(resource.toURI()), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					if (pathMatcher.matches(path.getFileName())) {
						try {
							String className = Paths.get(resource.toURI()).relativize(path).toString().replace(File.separatorChar, '.');
							classes.add(packageName + '.' + className.substring(0, className.length() - 6));
						} catch (URISyntaxException e) {
							throw new IllegalStateException(e);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
		return classes;
	}

	private static Integer compareVersionedMigrations(Path path) {
		Matcher filenameMatcher = FILENAME_PATTERN.matcher(path.getFileName().toString());
		if (filenameMatcher.find()) {
			return Integer.valueOf(filenameMatcher.group(1));
		} else {
			return Integer.MIN_VALUE;
		}
	}
}
