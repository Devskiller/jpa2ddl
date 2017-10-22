package com.devskiller.jpa2ddl;

import java.io.File;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateMojoTest {

	@Rule
	public final TestResources resources = new TestResources();

	@Rule
	public final TestMavenRuntime maven = new TestMavenRuntime();

	@Test
	public void testBasic() throws Exception {
		File basedir = resources.getBasedir("basic");
		GenerateMojo generate = (GenerateMojo) maven.lookupConfiguredMojo(maven.newMavenSession(maven.readMavenProject(basedir)), maven.newMojoExecution("generate"));
		assertThat(generate.getPackages()).containsOnly("com.test.model");
	}

	@Test
	public void testFull() throws Exception {
		File basedir = resources.getBasedir("full");
		GenerateMojo generate = (GenerateMojo) maven.lookupConfiguredMojo(maven.newMavenSession(maven.readMavenProject(basedir)), maven.newMojoExecution("generate"));
		assertThat(generate.getPackages()).containsOnly("com.test.model", "com.test.entities");
	}
}