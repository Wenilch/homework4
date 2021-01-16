package ru.digitalhabbits.homework4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class LocalPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private final PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();

	private static final String PROPERTIES_PATH_PATTERN = "classpath:config/*.properties";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Arrays.stream(getLocalResources())
				.sorted(Comparator.comparing(Resource::getFilename))
				.map(this::loadProperty)
				.forEach(propertySource -> environment.getPropertySources().addLast(propertySource));
	}

	private Resource[] getLocalResources() {
		try {
			return new PathMatchingResourcePatternResolver().getResources(PROPERTIES_PATH_PATTERN);
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to load properties configuration.", exception);
		}
	}

	private PropertySource<?> loadProperty(Resource path) {
		if (!path.exists()) {
			throw new IllegalArgumentException("Resource " + path + " does not exist");
		}

		try {
			return propertiesPropertySourceLoader.load(path.getFilename(), path).get(0);
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to load property configuration from " + path, ex);
		}
	}
}
