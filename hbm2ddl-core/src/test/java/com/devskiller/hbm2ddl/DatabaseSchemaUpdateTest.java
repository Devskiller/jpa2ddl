package com.devskiller.hbm2ddl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import org.hibernate.dialect.MySQL57Dialect;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseSchemaUpdateTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldGenerateSchemaUpdate() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();

		Files.copy(Paths.get(getClass().getClassLoader().getResource("sample_migration/v1__init.sql").getPath()),
				outputPath.toPath().resolve("v1__init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", MySQL57Dialect.class.getCanonicalName());
		jpaProperties.setProperty("hibernate.default_schema", "prod");

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.hbm2ddl.sample"), Action.UPDATE, jpaProperties, true, ";"));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v2__jpa2ddl.sql")));
		assertThat(sql).containsIgnoringCase("alter table prod.User");
		assertThat(sql).containsIgnoringCase("add column email");
		assertThat(sql).doesNotContain("create table prod.User");
	}

	@Test
	public void shouldGenerateDefaultSchemaUpdate() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();

		Files.copy(Paths.get(getClass().getClassLoader().getResource("sample_default_migration/v1__init.sql").getPath()),
				outputPath.toPath().resolve("v1__init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", MySQL57Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.hbm2ddl.sample"), Action.UPDATE, jpaProperties, true, ";"));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v2__jpa2ddl.sql")));
		assertThat(sql).containsIgnoringCase("alter table User");
		assertThat(sql).containsIgnoringCase("add column email");
		assertThat(sql).doesNotContain("create table User");
	}

}