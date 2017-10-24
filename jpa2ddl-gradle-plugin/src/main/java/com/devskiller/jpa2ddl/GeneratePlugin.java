package com.devskiller.jpa2ddl;

import java.nio.file.Paths;
import java.util.Properties;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class GeneratePlugin implements Plugin<Project> {

	private static final String EXTENSION_NAME = "jpa2ddl";
	private static final String TASK_NAME = "generateDdl";

	@Override
	public void apply(Project project) {
		GeneratePluginExtension generatePluginExtension = project.getExtensions().create(EXTENSION_NAME,
				GeneratePluginExtension.class, project);

		GenerateTask generateTask = project.getTasks().create(TASK_NAME, GenerateTask.class);
		generateTask.setExtension(generatePluginExtension);

		project.afterEvaluate(evaluatedProject -> fillDefaults(generatePluginExtension));
	}

	private void fillDefaults(GeneratePluginExtension generatePluginExtension) {
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
		if (!generatePluginExtension.getJpaPropertiesProvider().isPresent()) {
			generatePluginExtension.setJpaProperties(new Properties());
		}
		if (!generatePluginExtension.getOutputPathProvider().isPresent()) {
			generatePluginExtension.setOutputPath(Paths.get("/tmp/dupa.sqsl").toFile());
		}
	}
}
