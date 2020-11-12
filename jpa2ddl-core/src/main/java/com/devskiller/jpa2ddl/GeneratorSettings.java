package com.devskiller.jpa2ddl;

import java.io.File;
import java.util.List;
import java.util.Properties;

class GeneratorSettings {

	private final GenerationMode generationMode;
	private final File outputPath;
	private final File queryDslOutputPath;
	private final List<String> packages;
	private final Action action;
	private final Properties jpaProperties;
	private final boolean formatOutput;
	private final String delimiter;
	private final boolean skipSequences;

	GeneratorSettings(GenerationMode generationMode, File outputPath, File queryDslOutputPath, List<String> packages, Action action,
	                  Properties jpaProperties, boolean formatOutput, String delimiter, boolean skipSequences) {
		this.generationMode = generationMode;
		this.outputPath = outputPath;
		this.queryDslOutputPath = queryDslOutputPath;
		this.packages = packages;
		this.action = action;
		this.jpaProperties = jpaProperties;
		this.formatOutput = formatOutput;
		this.delimiter = delimiter;
		this.skipSequences = skipSequences;
	}

	GenerationMode getGenerationMode() {
		return generationMode;
	}

	File getOutputPath() {
		return outputPath;
	}

	File getQueryDslOutputPath() {
		return queryDslOutputPath;
	}

	List<String> getPackages() {
		return packages;
	}

	Action getAction() {
		return action;
	}

	Properties getJpaProperties() {
		return jpaProperties;
	}

	boolean isFormatOutput() {
		return formatOutput;
	}

	String getDelimiter() {
		return delimiter;
	}

	public boolean isSkipSequences() {
		return skipSequences;
	}
}
