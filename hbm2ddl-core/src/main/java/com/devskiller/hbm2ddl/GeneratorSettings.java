package com.devskiller.hbm2ddl;

import java.io.File;
import java.util.List;
import java.util.Properties;

class GeneratorSettings {

	private final GenerationMode generationMode;
	private final File schemaFile;
	private final File migrationsDir;
	private final List<String> packages;
	private final Action action;
	private final Properties jpaProperties;
	private final boolean formatOutput;
	private final String delimiter;

	GeneratorSettings(GenerationMode generationMode, File schemaFile, File migrationsDir, List<String> packages, Action action,
	                  Properties jpaProperties, boolean formatOutput, String delimiter) {
		this.generationMode = generationMode;
		this.schemaFile = schemaFile;
		this.migrationsDir = migrationsDir;
		this.packages = packages;
		this.action = action;
		this.jpaProperties = jpaProperties;
		this.formatOutput = formatOutput;
		this.delimiter = delimiter;
	}

	GenerationMode getGenerationMode() {
		return generationMode;
	}

	File getSchemaFile() {
		return schemaFile;
	}

	public File getMigrationsDir() {
		return migrationsDir;
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
}
