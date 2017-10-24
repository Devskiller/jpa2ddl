package com.devskiller.jpa2ddl;

import java.util.Arrays;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GenerateTask extends DefaultTask {

	private GeneratePluginExtension extension;

	void setExtension(GeneratePluginExtension extension) {
		this.extension = extension;
	}

	@TaskAction
	public void generateModel() throws Exception {
		GeneratorSettings settings = getSettings();
		new SchemaGenerator().generate(settings);
	}

	GeneratorSettings getSettings() {
		return new GeneratorSettings(extension.getGenerationMode(),
				extension.getOutputPath(),
				Arrays.asList(extension.getPackages()),
				extension.getAction(),
				extension.getJpaProperties(),
				extension.getFormatOutput(),
				extension.getDelimiter()
		);
	}

}
