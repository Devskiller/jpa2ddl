package com.devskiller.jpa2ddl;

import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

import java.io.File;
import java.util.List;
import java.util.Map;

public class GeneratePluginExtension {

	private final RegularFileProperty outputPath;
	private final Property<GenerationMode> generationMode;
	private final ListProperty<String> packages;
	private final Property<Action> action;
	private final MapProperty<String, String> jpaProperties;
	private final Property<Boolean> formatOutput;
	private final Property<Boolean> skipSequences;
	private final Property<String> delimiter;
	private final MapProperty<String, String> processorProperties;

	public GeneratePluginExtension(Project project) {
		outputPath = project.getObjects().fileProperty();
		generationMode = project.getObjects().property(GenerationMode.class);
		packages = project.getObjects().listProperty(String.class);
		action = project.getObjects().property(Action.class);
		jpaProperties = project.getObjects().mapProperty(String.class, String.class);
		formatOutput = project.getObjects().property(Boolean.class);
		skipSequences = project.getObjects().property(Boolean.class);
		delimiter = project.getObjects().property(String.class);
		processorProperties = project.getObjects().mapProperty(String.class, String.class);
	}

	public File getOutputPath() {
		return outputPath.getAsFile().get();
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

	public List<String> getPackages() {
		return packages.get();
	}

	public void setPackages(List<String> packages) {
		this.packages.set(packages);
	}

	public Action getAction() {
		return action.get();
	}

	public void setAction(Action action) {
		this.action.set(action);
	}

	public Map<String, String> getJpaProperties() {
		return jpaProperties.get();
	}

	public void setJpaProperties(Map<String, String> jpaProperties) {
		this.jpaProperties.set(jpaProperties);
	}

	public Boolean getFormatOutput() {
		return formatOutput.get();
	}

	public void setFormatOutput(Boolean formatOutput) {
		this.formatOutput.set(formatOutput);
	}

	public Boolean getSkipSequences() {
		return skipSequences.get();
	}

	public void setSkipSequences(Boolean skipSequences) {
		this.skipSequences.set(skipSequences);
	}

	public String getDelimiter() {
		return delimiter.get();
	}

	public void setDelimiter(String delimiter) {
		this.delimiter.set(delimiter);
	}

	public Map<String, String> getProcessorProperties() {
		return processorProperties.get();
	}

	public void setProcessorProperties(Map<String, String> processorProperties) {
		this.processorProperties.set(processorProperties);
	}

	public Provider<File> getOutputPathProvider() {
		return outputPath.getAsFile();
	}

	public Provider<GenerationMode> getGenerationModeProvider() {
		return generationMode;
	}

	public Provider<List<String>> getPackagesProvider() {
		return packages;
	}

	public Provider<Action> getActionProvider() {
		return action;
	}

	public Provider<Map<String, String>> getJpaPropertiesProvider() {
		return jpaProperties;
	}

	public Provider<Map<String, String>> getProcessorPropertiesProvider() {
		return processorProperties;
	}

	public Provider<Boolean> getFormatOutputProvider() {
		return formatOutput;
	}

	public Provider<Boolean> getSkipSequencesProvider() {
		return skipSequences;
	}

	public Provider<String> getDelimiterProvider() {
		return delimiter;
	}
}
