package com.devskiller.hbm2ddl;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.Action;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}/generated-resources/scripts/database.sql")
	private File outputFile;

	@Parameter(required = true)
	private List<String> packages;

	@Parameter(required = true)
	private Properties jpaProperties;

	@Parameter(defaultValue = "true")
	private boolean formatOutput;

	@Parameter(defaultValue = ";")
	private String delimiter;

	@Parameter(defaultValue = "CREATE")
	private SchemaExport.Action action;

	public void execute() throws MojoExecutionException, MojoFailureException {
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		try {
			schemaGenerator.generate(outputFile, packages, action, jpaProperties, formatOutput, delimiter);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}
		getLog().info("Packages " + String.join(",", packages));
	}

	public List<String> getPackages() {
		return packages;
	}


}
