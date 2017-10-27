package com.devskiller.jpa2ddl;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GenerateTask extends DefaultTask {

	private GeneratePluginExtension extension;
	private Set<String> outputClassesDirs;

	void setExtension(GeneratePluginExtension extension) {
		this.extension = extension;
	}

	@TaskAction
	public void generateModel() throws Exception {
		getLogger().info("Running schema generation...");
		GeneratorSettings settings = getSettings();
		URL[] urls = outputClassesDirs.stream()
				.map(path -> {
					try {
						return new URL("file://" + path + "/");
					} catch (MalformedURLException e) {
						throw new IllegalStateException("Cannot build URL from sourceSets", e);
					}
				})
				.toArray(URL[]::new);
		ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader urlClassLoader = new URLClassLoader(urls, originalClassLoader);
		Thread.currentThread().setContextClassLoader(urlClassLoader);
		new SchemaGenerator().generate(settings);
		Thread.currentThread().setContextClassLoader(originalClassLoader);
		getLogger().info("Schema saved to " + extension.getOutputPath());
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

	public void setOutputClassesDirs(Set<String> outputClassesDirs) {
		this.outputClassesDirs = outputClassesDirs;
	}
}
