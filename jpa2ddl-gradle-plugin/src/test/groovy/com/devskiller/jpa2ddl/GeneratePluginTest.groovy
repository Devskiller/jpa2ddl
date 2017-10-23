package com.devskiller.jpa2ddl

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class GeneratePluginTest {

	@Test
	void shouldAddTaskToProject() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply 'com.devskiller.jpa2ddl'

		assertTrue(project.tasks.jpa2ddl instanceof GenerateTask)
	}
}
