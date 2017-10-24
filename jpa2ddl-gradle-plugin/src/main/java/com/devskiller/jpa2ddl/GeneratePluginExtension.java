package com.devskiller.jpa2ddl;

import java.io.File;
import java.util.Properties;

import org.gradle.api.Project;
import org.gradle.api.provider.PropertyState;
import org.gradle.api.provider.Provider;

public class GeneratePluginExtension {

	private final PropertyState<File> outputPath;
	private final PropertyState<GenerationMode> generationMode;
	private final PropertyState<String[]> packages;
	private final PropertyState<Action> action;
	private final PropertyState<Properties> jpaProperties;
	private final PropertyState<Boolean> formatOutput;
	private final PropertyState<String> delimiter;

	public GeneratePluginExtension(Project project) {
		outputPath = project.property(File.class);
		generationMode = project.property(GenerationMode.class);
		packages = project.property(String[].class);
		action = project.property(Action.class);
		jpaProperties = project.property(Properties.class);
		formatOutput = project.property(Boolean.class);
		delimiter = project.property(String.class);
	}

	public File getOutputPath() {
		return outputPath.get();
	}

	public void setOutputPath(File outputPath) {
		this.outputPath.set(outputPath);
	}

	public GenerationMode getGenerationMode() {
		return generationMode.get();
	}

	public void setGenerationMode(GenerationMode generationMode) {
		this.generationMode.set(generationMode);
	}

	public String[] getPackages() {
		return packages.get();
	}

	public void setPackages(String[] packages) {
		this.packages.set(packages);
	}

	public Action getAction() {
		return action.get();
	}

	public void setAction(Action action) {
		this.action.set(action);
	}

	public Properties getJpaProperties() {
		return jpaProperties.get();
	}

	public void setJpaProperties(Properties jpaProperties) {
		this.jpaProperties.set(jpaProperties);
	}

	public Boolean getFormatOutput() {
		return formatOutput.get();
	}

	public void setFormatOutput(Boolean formatOutput) {
		this.formatOutput.set(formatOutput);
	}

	public String getDelimiter() {
		return delimiter.get();
	}

	public void setDelimiter(String delimiter) {
		this.delimiter.set(delimiter);
	}

	public Provider<File> getOutputPathProvider() {
		return outputPath;
	}

	public Provider<GenerationMode> getGenerationModeProvider() {
		return generationMode;
	}

	public Provider<String[]> getPackagesProvider() {
		return packages;
	}

	public Provider<Action> getActionProvider() {
		return action;
	}

	public Provider<Properties> getJpaPropertiesProvider() {
		return jpaProperties;
	}

	public Provider<Boolean> getFormatOutputProvider() {
		return formatOutput;
	}

	public Provider<String> getDelimiterProvider() {
		return delimiter;
	}
}
