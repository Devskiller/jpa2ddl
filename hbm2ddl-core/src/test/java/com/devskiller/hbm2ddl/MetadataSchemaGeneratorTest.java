package com.devskiller.hbm2ddl;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataSchemaGeneratorTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldGenerateSchemaFromMetadataWithDropAndCreate() throws Exception {
		// given
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File outputFile = tempFolder.newFile();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.METADATA, outputFile,
				Arrays.asList("com.devskiller.hbm2ddl.sample"), Action.DROP_AND_CREATE, new Properties(), true, ";"));

		// then
		String sql = new String(Files.readAllBytes(outputFile.toPath()));
		assertThat(sql).contains("create table User");
		assertThat(sql).contains("drop table User");
	}

	@Test
	public void shouldGenerateSchemaFromMetadataWithDrop() throws Exception {
		// given
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File outputFile = tempFolder.newFile();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.METADATA, outputFile,
				Arrays.asList("com.devskiller.hbm2ddl.sample"), Action.DROP, new Properties(), true, ";"));

		// then
		String sql = new String(Files.readAllBytes(outputFile.toPath()));
		assertThat(sql).doesNotContain("create table User");
		assertThat(sql).contains("drop table User");
	}

	@Test
	public void shouldGenerateSchemaFromMetadataWithCreate() throws Exception {
		// given
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File outputFile = tempFolder.newFile();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.METADATA, outputFile,
				Arrays.asList("com.devskiller.hbm2ddl.sample"), Action.CREATE, new Properties(), true, ";"));

		// then
		String sql = new String(Files.readAllBytes(outputFile.toPath()));
		assertThat(sql).contains("create table User");
		assertThat(sql).doesNotContain("drop table User");
	}

	@Test
	public void shouldGenerateSchemaFromH2() throws Exception {

	}
}