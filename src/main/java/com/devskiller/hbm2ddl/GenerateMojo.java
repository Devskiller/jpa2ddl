package com.devskiller.hbm2ddl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.hibernate.tool.hbm2ddl.SchemaExport;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}/generated-resources/scripts/database.sql")
	private File outputFile;

	@Parameter(required = true)
	private List<String> packages;

	@Parameter
	private Properties jpaProperties;

	@Parameter(defaultValue = "true")
	private boolean formatOutput;

	@Parameter(defaultValue = ";")
	private String delimiter;

	@Parameter(defaultValue = "CREATE")
	private SchemaExport.Action action;

	@Parameter( defaultValue = "${project}", readonly = true )
	private MavenProject project;

	@Parameter( defaultValue = "${plugin}", readonly = true )
	private PluginDescriptor descriptor;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Running schema generation...");
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		List<String> compileSourceRoots = project.getCompileSourceRoots();
		compileSourceRoots.stream().map(this::mapPathToURL).forEach(url -> descriptor.getClassRealm().addURL(url));
		try {
			project.getCompileClasspathElements().stream().map(this::mapPathToURL).forEach(url -> descriptor.getClassRealm().addURL(url));
		} catch (DependencyResolutionRequiredException e) {
			throw new IllegalStateException(e);
		}
		try {
			schemaGenerator.generate(outputFile, packages, action, jpaProperties, formatOutput, delimiter);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private URL mapPathToURL(String path) {
		try {
			return Paths.get(path).toUri().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<String> getPackages() {
		return packages;
	}

}
