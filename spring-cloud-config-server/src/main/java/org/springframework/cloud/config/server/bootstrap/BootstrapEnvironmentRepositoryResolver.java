package org.springframework.cloud.config.server.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;

/**
 * Environment Repository Resolver when bootstrap is enabled.
 */
public class BootstrapEnvironmentRepositoryResolver {
	private final DefaultListableBeanFactory beanFactory;

	public BootstrapEnvironmentRepositoryResolver(final DefaultListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Retrieve a {@link EnvironmentRepository} for bootstrap.
	 *
	 * @return a {@link EnvironmentRepository} instance
	 */
	public EnvironmentRepository retrieveRepository() {
		final List<String> beanNames = Arrays.asList(this.beanFactory.getBeanNamesForType(EnvironmentRepository.class));

		final List<EnvironmentRepository> beans = new ArrayList<>();
		for (final String beanName : beanNames) {
			final EnvironmentRepository repo = EnvironmentRepository.class.cast(beanFactory.getBean(beanName));
			if (repo instanceof SearchPathLocator) {
				beans.add(repo);
			}
		}

		return beans.size() == 1 ? beans.get(0) : new CompositeEnvironmentRepository(beans);
	}
}
