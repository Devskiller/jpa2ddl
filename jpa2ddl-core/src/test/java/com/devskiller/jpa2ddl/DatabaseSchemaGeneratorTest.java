package com.devskiller.jpa2ddl;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseSchemaGeneratorTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldGenerateSchemaFromDatabaseWithDropAndCreate() throws Exception {
		// given
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File outputFile = tempFolder.newFile();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputFile,
				outputFile, null, Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.DROP_AND_CREATE, new Properties(), true, ";", false));

		// then
		String sql = new String(Files.readAllBytes(outputFile.toPath()));
		assertThat(sql).contains("create table User");
		assertThat(sql).contains("drop table User");
	}

	@Test
	public void shouldGenerateSchemaFromDatabaseWithDrop() throws Exception {
		// given
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File outputFile = tempFolder.newFile();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputFile,
				outputFile, null, Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.DROP, new Properties(), true, ";", false));

		// then
		String sql = new String(Files.readAllBytes(outputFile.toPath()));
		assertThat(sql).doesNotContain("create table User");
		assertThat(sql).contains("drop table User");
	}

	@Test
	public void shouldGenerateSchemaFromDatabaseWithCreate() throws Exception {
		// given
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File outputFile = tempFolder.newFile();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputFile,
				outputFile, null, Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.CREATE, new Properties(), true, ";", false));

		// then
		String sql = new String(Files.readAllBytes(outputFile.toPath()));
		assertThat(sql).contains("create table User");
		assertThat(sql).doesNotContain("drop table User");
	}

}
