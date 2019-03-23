package org.springframework.cloud.config.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.test.util.ReflectionTestUtils;

public class HealthIndicatorEnvironmentRepositoryResolverTests {

	private DefaultListableBeanFactory mockDefaultListableBeanFactory;
	private HealthIndicatorEnvironmentRepositoryResolver testClass;

	@Before
	public void before() {
		this.mockDefaultListableBeanFactory = mock(DefaultListableBeanFactory.class);

		this.testClass = new HealthIndicatorEnvironmentRepositoryResolver(this.mockDefaultListableBeanFactory);
	}

	@Test
	public void testNativeRepo() {
		EnvironmentRepository mockNativeRepo = mock(NativeEnvironmentRepository.class);

		doReturn(new String[]{"mockNativeRepo"}).when(this.mockDefaultListableBeanFactory)
			.getBeanNamesForType(any(Class.class));
		doReturn(mockNativeRepo).when(this.mockDefaultListableBeanFactory).getBean(eq("mockNativeRepo"));

		EnvironmentRepository repo = this.testClass.retrieveRepository();
		assertEquals(mockNativeRepo, repo);

		verify(this.mockDefaultListableBeanFactory, times(1)).getBeanNamesForType(eq(EnvironmentRepository.class));
		verify(this.mockDefaultListableBeanFactory, times(1)).getBean(eq("mockNativeRepo"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCompositeRepos() {
		EnvironmentRepository mockNativeRepo = mock(NativeEnvironmentRepository.class);
		EnvironmentRepository mockGitRepo = mock(JGitEnvironmentRepository.class);

		doReturn(new String[]{"mockNativeRepo", "mockGitRepo"}).when(this.mockDefaultListableBeanFactory)
			.getBeanNamesForType(any(Class.class));

		doReturn(mockNativeRepo).when(this.mockDefaultListableBeanFactory).getBean(eq("mockNativeRepo"));
		doReturn(mockGitRepo).when(this.mockDefaultListableBeanFactory).getBean(eq("mockGitRepo"));

		EnvironmentRepository repo = this.testClass.retrieveRepository();
		assertTrue(repo instanceof CompositeEnvironmentRepository);
		assertNotEquals(mockNativeRepo, repo);
		assertNotEquals(mockGitRepo, repo);

		List<EnvironmentRepository> repos = (List<EnvironmentRepository>) ReflectionTestUtils
			.getField(repo, "environmentRepositories");
		assertEquals(2, repos.size());
		assertEquals(mockNativeRepo, repos.get(0));
		assertEquals(mockGitRepo, repos.get(1));

		verify(this.mockDefaultListableBeanFactory, times(1)).getBeanNamesForType(eq(EnvironmentRepository.class));
		verify(this.mockDefaultListableBeanFactory, times(1)).getBean(eq("mockNativeRepo"));
		verify(this.mockDefaultListableBeanFactory, times(1)).getBean(eq("mockGitRepo"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCompositeReposUsesExistingCompositeRepo() {
		EnvironmentRepository mockNativeRepo = mock(NativeEnvironmentRepository.class);
		EnvironmentRepository mockGitRepo = mock(JGitEnvironmentRepository.class);
		EnvironmentRepository mockCompositeRepo = mock(CompositeEnvironmentRepository.class);

		doReturn(new String[]{"mockNativeRepo", "mockGitRepo", "mockCompositeRepo"})
			.when(this.mockDefaultListableBeanFactory)
			.getBeanNamesForType(any(Class.class));

		doReturn(mockNativeRepo).when(this.mockDefaultListableBeanFactory).getBean(eq("mockNativeRepo"));
		doReturn(mockGitRepo).when(this.mockDefaultListableBeanFactory).getBean(eq("mockGitRepo"));
		doReturn(mockCompositeRepo).when(this.mockDefaultListableBeanFactory).getBean("mockCompositeRepo");

		EnvironmentRepository repo = this.testClass.retrieveRepository();
		assertTrue(repo instanceof CompositeEnvironmentRepository);
		assertNotEquals(mockNativeRepo, repo);
		assertNotEquals(mockGitRepo, repo);
		assertEquals(mockCompositeRepo, repo);

		verify(this.mockDefaultListableBeanFactory, times(1)).getBeanNamesForType(eq(EnvironmentRepository.class));
		verify(this.mockDefaultListableBeanFactory, times(1)).getBean(eq("mockNativeRepo"));
		verify(this.mockDefaultListableBeanFactory, times(1)).getBean(eq("mockGitRepo"));
		verify(this.mockDefaultListableBeanFactory, times(1)).getBean(eq("mockCompositeRepo"));
	}
}
