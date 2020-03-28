package com.devskiller.jpa2ddl;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.file.UnionFileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

class GeneratePlugin implements Plugin<Project> {

	private static final String EXTENSION_NAME = "jpa2ddl";
	private static final String TASK_NAME = "generateDdl";

	@Override
	public void apply(Project project) {
		GeneratePluginExtension generatePluginExtension = project.getExtensions().create(EXTENSION_NAME,
				GeneratePluginExtension.class, project);

		GenerateTask generateTask = project.getTasks().create(TASK_NAME, GenerateTask.class);
		generateTask.setGroup(BasePlugin.BUILD_GROUP);
		generateTask.setDescription("Generates DDL scripts based on JPA model.");
		generateTask.setExtension(generatePluginExtension);
		generateTask.dependsOn(JavaBasePlugin.BUILD_TASK_NAME);


		project.afterEvaluate(evaluatedProject -> {
			fillDefaults(evaluatedProject, generatePluginExtension);
			SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
			Set<File> paths;
			if (sourceSets != null) {
				UnionFileCollection mainClasspath = (UnionFileCollection) sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath();
				paths = mainClasspath.getFiles();
			} else {
				paths = new HashSet<>();
			}
			generateTask.setOutputClassesDirs(paths);
		});
	}

	private void fillDefaults(Project project, GeneratePluginExtension generatePluginExtension) {
		if (!generatePluginExtension.getPackagesProvider().isPresent()) {
			throw new IllegalArgumentException("JPA2DDL: No packages property found - it must point to model packages");
		}
		if (!generatePluginExtension.getActionProvider().isPresent()) {
			generatePluginExtension.setAction(Action.CREATE);
		}
		if (!generatePluginExtension.getGenerationModeProvider().isPresent()) {
			generatePluginExtension.setGenerationMode(GenerationMode.DATABASE);
		}
		if (!generatePluginExtension.getDelimiterProvider().isPresent()) {
			generatePluginExtension.setDelimiter(";");
		}
		if (!generatePluginExtension.getFormatOutputProvider().isPresent()) {
			generatePluginExtension.setFormatOutput(true);
		}
		if (!generatePluginExtension.getSkipSequencesProvider().isPresent()) {
			generatePluginExtension.setSkipSequences(false);
		}
		if (!generatePluginExtension.getJpaPropertiesProvider().isPresent()) {
			generatePluginExtension.setJpaProperties(new HashMap<>());
		}
		if (!generatePluginExtension.getOutputPathProvider().isPresent()) {
			String filePath = generatePluginExtension.getAction() == Action.UPDATE ? "scripts/" : "scripts/database.sql";
			generatePluginExtension.setOutputPath(project.getBuildDir().toPath().resolve("generated-resources/main/" + filePath).toFile());
		}
	}
}
