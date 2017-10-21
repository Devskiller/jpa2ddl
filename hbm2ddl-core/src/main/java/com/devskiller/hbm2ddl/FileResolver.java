package com.devskiller.hbm2ddl;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Pattern;

class FileResolver {

	private final static Pattern FILENAME_PATTERN = Pattern.compile("v([0-9]+)\\.sql");

	static File resolveNextMigrationFile(File migrationDir) {
		if (!migrationDir.exists()) {
			migrationDir.mkdirs();
		}

		Comparator<String> reversed = (Comparator<String>) Comparator.naturalOrder().reversed();
		String[] list = migrationDir.list();
		Optional<String> lastFile;
		if (list != null) {
			lastFile = Arrays.stream(list)
					.sorted(reversed)
					.findFirst();
		} else {
			lastFile = Optional.empty();
		}
		Integer fileIndex = lastFile.map(FILENAME_PATTERN::matcher).map(matcher -> {
			if (matcher.find()) {
				return Integer.valueOf(matcher.group(1));
			} else {
				return 0;
			}
		}).orElse(0);

		return migrationDir.toPath().resolve("v" + ++fileIndex + ".sql").toFile();
	}
}
