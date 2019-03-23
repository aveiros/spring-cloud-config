package org.springframework.cloud.config.server.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;

/**
 * Environment Repository Resolver for health checks.
 */
public class HealthIndicatorEnvironmentRepositoryResolver {
	private final DefaultListableBeanFactory beanFactory;

	public HealthIndicatorEnvironmentRepositoryResolver(final DefaultListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Retrieve a {@link EnvironmentRepository} for health.
	 *
	 * @return a {@link EnvironmentRepository} instance
	 */
	public EnvironmentRepository retrieveRepository() {
		final List<String> beanNames = Arrays.asList(this.beanFactory.getBeanNamesForType(EnvironmentRepository.class));

		final List<EnvironmentRepository> beans = new ArrayList<>();
		for (final String beanName : beanNames) {
			final EnvironmentRepository repo = EnvironmentRepository.class.cast(this.beanFactory.getBean(beanName));
			if (repo instanceof CompositeEnvironmentRepository) {
				return repo;
			}

			beans.add(repo);
		}

		return beans.size() == 1 ? beans.get(0) : new CompositeEnvironmentRepository(beans);
	}
}
