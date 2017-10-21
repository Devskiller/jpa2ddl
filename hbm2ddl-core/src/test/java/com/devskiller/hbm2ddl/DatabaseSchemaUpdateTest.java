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
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		File schemaFile = Paths.get("src/test/resources/sample/flat_database.sql").toFile();
		File migrationsDir = tempFolder.newFolder();

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", MySQL57Dialect.class.getCanonicalName());
		jpaProperties.setProperty("hibernate.default_schema", "prod");

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.DATABASE, schemaFile,
				migrationsDir, Arrays.asList("com.devskiller.hbm2ddl.sample"), Action.UPDATE, jpaProperties, true, ";"));

		// then
		String sql = new String(Files.readAllBytes(migrationsDir.toPath().resolve("v1.sql")));
		assertThat(sql).containsIgnoringCase("alter table prod.User");
		assertThat(sql).containsIgnoringCase("add column email");
		assertThat(sql).doesNotContain("create table prod.User");
	}

}