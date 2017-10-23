package com.devskiller.jpa2ddl

import org.gradle.api.Plugin
import org.gradle.api.Project

class GeneratePlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.task("jpa2ddl", type: GenerateTask)
	}
}