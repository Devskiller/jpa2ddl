package com.devskiller.jpa2ddl;

import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.util.ConfigurationBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

class ClassResolver {

	private final Reflections reflections;

	ClassResolver() {
		ConfigurationBuilder configuration = ConfigurationBuilder.build(".*")
				.setExpandSuperTypes(false)
				.setScanners(new SubTypesScanner(true));
		configuration.setUrls(getExistingUrls(configuration.getUrls()));
		this.reflections = new Reflections(configuration);
	}

	<T> Set<Class<? extends T>> getSubTypesOf(Class<T> aClass) {
		return reflections.getSubTypesOf(aClass);
	}

	private Set<URL> getExistingUrls(Set<URL> urls) {
		return urls.stream()
				.filter(url -> {
					try {
						return new File(url.toURI()).exists();
					} catch (URISyntaxException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toSet());
	}
}
