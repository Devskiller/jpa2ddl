package com.devskiller.jpa2ddl;

import org.hibernate.dialect.PostgreSQL10Dialect;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class ContainerDatabaseSchemaUpdateTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldGenerateSchemaFromDatabaseWithUpdateWithPostgresDialect() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", PostgreSQL10Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.CONTAINER_DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", false, null));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v1__jpa2ddl.sql")));
		System.out.println(sql);
		assertThat(sql).contains("create table User");
		assertThat(sql).doesNotContain("drop table User");
	}

	@Test
	public void shouldGenerateDefaultSchemaUpdateWithPostgresDialect() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();

		Files.copy(Paths.get(getClass().getClassLoader().getResource("container_postgres_migration/v1__jpa2ddl_init.sql").toURI()),
				outputPath.toPath().resolve("v1__jpa2ddl_init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", PostgreSQL10Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.CONTAINER_DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.sample"), Action.UPDATE, jpaProperties, true, ";", true, null));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v2__jpa2ddl.sql")));
		assertThat(sql).containsIgnoringCase("alter table if exists User");
		assertThat(sql).containsIgnoringCase("add column email");
		assertThat(sql).doesNotContain("create table User");
		assertThat(sql).doesNotContain("create sequence");
	}

}
