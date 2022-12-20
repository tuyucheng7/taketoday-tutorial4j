package cn.tuyucheng.taketoday.autoconfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalOnClassIntegrationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	void whenDependentClassIsPresent_thenBeanCreated() {
		this.contextRunner.withUserConfiguration(ConditionalOnClassConfiguration.class)
			.run(context -> {
				assertThat(context).hasBean("created");
				assertThat(context.getBean("created")).isEqualTo("This is created when ConditionalOnClassIntegrationTest is present on the classpath");
			});
	}

	@Test
	void whenDependentClassIsPresent_thenBeanMissing() {
		this.contextRunner.withUserConfiguration(ConditionalOnMissingClassConfiguration.class)
			.run(context -> {
				assertThat(context).doesNotHaveBean("missed");
			});
	}

	@Test
	void whenDependentClassIsNotPresent_thenBeanMissing() {
		this.contextRunner.withUserConfiguration(ConditionalOnClassConfiguration.class)
			.withClassLoader(new FilteredClassLoader(ConditionalOnClassIntegrationTest.class))
			.run((context) -> {
				assertThat(context).doesNotHaveBean("created");
				assertThat(context).doesNotHaveBean(ConditionalOnClassIntegrationTest.class);

			});
	}

	@Test
	void whenDependentClassIsNotPresent_thenBeanCreated() {
		this.contextRunner.withUserConfiguration(ConditionalOnMissingClassConfiguration.class)
			.withClassLoader(new FilteredClassLoader(ConditionalOnClassIntegrationTest.class))
			.run((context) -> {
				assertThat(context).hasBean("missed");
				assertThat(context).getBean("missed")
					.isEqualTo("This is missed when ConditionalOnClassIntegrationTest is present on the classpath");
				assertThat(context).doesNotHaveBean(ConditionalOnClassIntegrationTest.class);

			});
	}

	@Configuration
	@ConditionalOnClass(ConditionalOnClassIntegrationTest.class)
	protected static class ConditionalOnClassConfiguration {
		@Bean
		String created() {
			return "This is created when ConditionalOnClassIntegrationTest is present on the classpath";
		}
	}

	@Configuration
	@ConditionalOnMissingClass("cn.tuyucheng.taketoday.autoconfiguration.ConditionalOnClassIntegrationTest")
	protected static class ConditionalOnMissingClassConfiguration {
		@Bean
		String missed() {
			return "This is missed when ConditionalOnClassIntegrationTest is present on the classpath";
		}
	}
}