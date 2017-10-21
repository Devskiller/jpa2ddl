package com.devskiller.hbm2ddl;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class FileResolverTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldResolveFirstMigration() throws Exception {
		// given
		File migrationsDir = tempFolder.newFolder();

		// when
		File file = FileResolver.resolveNextMigrationFile(migrationsDir);

		// then
		assertThat(file.toPath()).isEqualTo(migrationsDir.toPath().resolve("v1.sql"));
	}

	@Test
	public void shouldResolveNextMigration() throws Exception {
		// given
		File migrationsDir = tempFolder.newFolder();
		migrationsDir.toPath().resolve("v1.sql").toFile().createNewFile();
		migrationsDir.toPath().resolve("v2.sql").toFile().createNewFile();

		// when
		File file = FileResolver.resolveNextMigrationFile(migrationsDir);

		// then
		assertThat(file.toPath()).isEqualTo(migrationsDir.toPath().resolve("v3.sql"));
	}

}