package com.devskiller.hbm2ddl;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class FileResolverTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldReturnExistingMigrations() throws Exception {
		// given
		File migrationsDir = tempFolder.newFolder();

		Path first = migrationsDir.toPath().resolve("v1__jpa2ddl.sql");
		first.toFile().createNewFile();
		Path second = migrationsDir.toPath().resolve("v2__my_description.sql");
		second.toFile().createNewFile();

		// when
		List<Path> file = FileResolver.resolveExistingMigrations(migrationsDir, false);

		// then
		assertThat(file).containsSequence(
				first,
				second
		);
	}

	@Test
	public void shouldResolveFirstMigration() throws Exception {
		// given
		File migrationsDir = tempFolder.newFolder();

		// when
		File file = FileResolver.resolveNextMigrationFile(migrationsDir);

		// then
		assertThat(file.toPath()).isEqualTo(migrationsDir.toPath().resolve("v1__jpa2ddl.sql"));
	}

	@Test
	public void shouldResolveNextMigration() throws Exception {
		// given
		File migrationsDir = tempFolder.newFolder();
		migrationsDir.toPath().resolve("v1__jpa2ddl.sql").toFile().createNewFile();
		migrationsDir.toPath().resolve("v2__my_description.sql").toFile().createNewFile();

		// when
		File file = FileResolver.resolveNextMigrationFile(migrationsDir);

		// then
		assertThat(file.toPath()).isEqualTo(migrationsDir.toPath().resolve("v3__jpa2ddl.sql"));
	}

}