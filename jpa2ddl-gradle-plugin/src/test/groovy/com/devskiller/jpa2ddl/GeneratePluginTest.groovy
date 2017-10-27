package com.devskiller.jpa2ddl

import java.nio.file.Files

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.assertj.core.api.Assertions.assertThat

class GeneratePluginTest {

	@Rule
	public final TemporaryFolder testProjectDir = new TemporaryFolder()

	@Test
	void shouldAddTaskToProject() {
		File buildFile = testProjectDir.newFile("build.gradle")

		Files.write(buildFile.toPath(), "jpa2ddl {packages=['com.test']\njpaProperties=['hibernate.dialect':'org.hibernate.dialect.H2Dialect']}".getBytes())

		Project project = ProjectBuilder.builder()
				.withProjectDir(testProjectDir.getRoot())
				.build()

		project.pluginManager.apply 'com.devskiller.jpa2ddl'

		((ProjectInternal) project).evaluate()

		GenerateTask task = (GenerateTask) project.tasks.generateDdl

		GeneratorSettings settings = task.getSettings()
		assertThat(settings.getAction()).isEqualTo(Action.CREATE)
		assertThat(settings.getDelimiter()).isEqualTo(";")
		assertThat(settings.getGenerationMode()).isEqualTo(GenerationMode.DATABASE)
		assertThat(settings.getJpaProperties()).contains(new MapEntry("hibernate.dialect", "org.hibernate.dialect.H2Dialect"))
		assertThat(settings.getPackages()).containsOnly("com.test")
		assertThat(settings.getOutputPath()).isEqualTo(testProjectDir.getRoot().toPath().resolve("build/generated-resources/main/scripts/database.sql").toFile())
	}
}
