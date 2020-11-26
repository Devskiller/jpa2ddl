package com.devskiller.jpa2ddl;

import com.devskiller.jpa2ddl.dialects.H2MySQL57Dialect;
import com.devskiller.jpa2ddl.dialects.H2PostgreSQL95Dialect;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseComplexSchemaUpdateTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	@Ignore("should be resolved by introducing test containers in #40")
	public void shouldGenerateSchemaUpdate() throws Exception {
		// given
		File outputPath = tempFolder.newFolder();

		Files.copy(Paths.get(getClass().getClassLoader().getResource("complex_migration/v1__jpa2ddl_init.sql").toURI()),
				outputPath.toPath().resolve("v1__jpa2ddl_init.sql"));

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", H2MySQL57Dialect.class.getCanonicalName());

		SchemaGenerator schemaGenerator = new SchemaGenerator();

		// when
		schemaGenerator.generate(new GeneratorSettings(GenerationMode.EMBEDDED_DATABASE, outputPath,
				Arrays.asList("com.devskiller.jpa2ddl.complex"), Action.UPDATE, jpaProperties, true, ";", false, null));

		// then
		String sql = new String(Files.readAllBytes(outputPath.toPath().resolve("v2__jpa2ddl.sql")));
		assertThat(sql).doesNotContain("alter table Book_Chapter");
		assertThat(sql).doesNotContain("drop index");
		assertThat(sql).doesNotContain("add constraint");
	}

}
