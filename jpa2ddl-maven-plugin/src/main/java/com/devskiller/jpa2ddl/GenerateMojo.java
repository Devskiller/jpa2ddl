package com.devskiller.jpa2ddl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import static java.util.Objects.isNull;

/**
 * Goal to generate database schema based on the JPA entities
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractMojo {

	/**
	 * Output path for the generated schema.
	 */
	@Parameter
	private File outputPath;

	/**
	 * List of packages containing JPA entities
	 */
	@Parameter(required = true)
	private List<String> packages;

	/**
	 * Additional properties like dialect or naming strategies which should be used in generation task
	 */
	@Parameter
	private Properties jpaProperties;

	/**
	 * Should we format the output
	 */
	@Parameter(defaultValue = "true")
	private boolean formatOutput;

	/**
	 * Should we skip sequence generation
	 */
	@Parameter(defaultValue = "false")
	private boolean skipSequences;

	/**
	 * Delimiter used to separate statements
	 */
	@Parameter(defaultValue = ";")
	private String delimiter;

	/**
	 * Action describing which statements should be generated. Possible values:
	 * CREATE
	 * DROP
	 * DROP_AND_CREATE
	 * UPDATE
	 */
	@Parameter(defaultValue = "CREATE")
	private Action action;

	/**
	 * Schema generation mode. Possible values:
	 * DATABASE - Default - generation based on setting up embedded database and dumping the schema.
	 * METADATA - generation based on static metadata
	 */
	@Parameter(defaultValue = "DATABASE")
	private GenerationMode generationMode;

	/**
	 * Additional properties for external processors
	 */
	@Parameter
	private Properties processorProperties;

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Parameter(defaultValue = "${plugin}", readonly = true)
	private PluginDescriptor descriptor;

	public void execute() throws MojoExecutionException {
		getLog().info("Running schema generation...");
		if (isNull(jpaProperties)) {
			jpaProperties = new Properties();
		}

		if (outputPath == null) {
			if (action == Action.UPDATE) {
				outputPath = Paths.get(project.getBuild().getDirectory()).resolve("generated-resources/scripts").toFile();
			} else {
				outputPath = Paths.get(project.getBuild().getDirectory()).resolve("generated-resources/scripts/database.sql").toFile();
			}
		}

		SchemaGenerator schemaGenerator = new SchemaGenerator();
		List<String> compileSourceRoots = project.getCompileSourceRoots();
		compileSourceRoots.stream().map(this::mapPathToURL).forEach(url -> descriptor.getClassRealm().addURL(url));
		try {
			project.getCompileClasspathElements().stream().map(this::mapPathToURL).forEach(url -> descriptor.getClassRealm().addURL(url));
		} catch (DependencyResolutionRequiredException e) {
			throw new IllegalStateException(e);
		}

		GeneratorSettings settings = new GeneratorSettings(
				generationMode, outputPath, packages, action, jpaProperties, formatOutput, delimiter, skipSequences, processorProperties);
		try {
			schemaGenerator.generate(settings);
			getLog().info("Schema saved to " + outputPath);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private URL mapPathToURL(String path) {
		try {
			return Paths.get(path).toUri().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	List<String> getPackages() {
		return packages;
	}
}
