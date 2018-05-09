package com.devskiller.jpa2ddl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
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

		Files.copy(Paths.get(getClass().getClassLoader().getResource("sample_migration/v1__jpa2ddl_init.sql").toURI()),
				outputPath.toPath().resolve("v1__jpa2ddl_init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", MySQL57Dialect.class.getCanonicalName());
		jpaProperties.setProperty("hibernate.default_schema", "prod");

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", false));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v2__jpa2ddl.sql")));
		assertThat(sql).containsIgnoringCase("alter table prod.User");
		assertThat(sql).containsIgnoringCase("add column email varchar(255);");
		assertThat(sql).containsIgnoringCase("create table prod.hibernate_sequence");
		assertThat(sql).containsIgnoringCase("insert into prod.hibernate_sequence values");
		assertThat(sql).doesNotContain("create table prod.User");
	}

	@Test
	public void shouldGenerateDefaultSchemaUpdate() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();

		Files.copy(Paths.get(getClass().getClassLoader().getResource("sample_default_migration/v1__jpa2ddl_init.sql").toURI()),
				outputPath.toPath().resolve("v1__jpa2ddl_init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", MySQL57Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", true));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v2__jpa2ddl.sql")));
		assertThat(sql).containsIgnoringCase("alter table User");
		assertThat(sql).containsIgnoringCase("add column email");
		assertThat(sql).doesNotContain("create table User");
		assertThat(sql).doesNotContain("create table hibernate_sequence");
		assertThat(sql).doesNotContain("insert into hibernate_sequence values");
	}


	@Test
	public void shouldSkipGenerationIfNoChanges() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();

		Files.copy(Paths.get(getClass().getClassLoader().getResource("sample_empty_migration/v1__jpa2ddl_init.sql").toURI()),
				outputPath.toPath().resolve("v1__jpa2ddl_init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", MySQL57Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", true));

		// then
		File migrationFile = outputPath.toPath().resolve("v2__jpa2ddl.sql").toFile();
		assertThat(migrationFile).doesNotExist();
	}

	@Test
	public void shouldGenerateSchemaFromDatabaseWithUpdateWithPostgresDialect() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", PostgreSQL9Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", false));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v1__jpa2ddl.sql")));
		assertThat(sql).contains("create table User");
		assertThat(sql).doesNotContain("drop table User");
	}

	@Test
	public void shouldGenerateSchemaFromDatabaseWithUpdateWithOracleDialect() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", Oracle12cDialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", false));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v1__jpa2ddl.sql")));
		assertThat(sql).contains("create table User");
		assertThat(sql).doesNotContain("drop table User");
	}


}