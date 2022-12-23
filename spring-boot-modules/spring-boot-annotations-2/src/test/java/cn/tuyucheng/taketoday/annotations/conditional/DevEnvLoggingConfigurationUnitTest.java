package cn.tuyucheng.taketoday.annotations.conditional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DevEnvLoggingConfigurationUnitTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	void whenDevEnvEnabled_thenDevEnvLoggingConfigurationAndLoggingServiceShouldBeCreated() {
		System.setProperty("env", "dev");

		contextRunner
			.withUserConfiguration(ConditionalTestConfiguration.class)
			.run(context ->
				assertNotNull(
					context.getBean(DevEnvLoggingConfiguration.class)
				)
			);
		contextRunner
			.withUserConfiguration(ConditionalTestConfiguration.class)
			.run(context ->
				assertNotNull(
					context.getBean(LoggingService.class)
				)
			);
	}

	@Test
	void whenDevEnvNotEnabled_thenDevEnvLoggingConfigurationAndLoggingServiceShouldNotBeCreated() {
		System.setProperty("env", "not-dev");

		contextRunner
			.withUserConfiguration(ConditionalTestConfiguration.class)
			.run(context ->
				assertThrows(NoSuchBeanDefinitionException.class, () ->
					context.getBean(DevEnvLoggingConfiguration.class)
				)
			);
		contextRunner
			.withUserConfiguration(ConditionalTestConfiguration.class)
			.run(context ->
				assertThrows(NoSuchBeanDefinitionException.class, () ->
					context.getBean(LoggingService.class)
				)
			);
	}
}