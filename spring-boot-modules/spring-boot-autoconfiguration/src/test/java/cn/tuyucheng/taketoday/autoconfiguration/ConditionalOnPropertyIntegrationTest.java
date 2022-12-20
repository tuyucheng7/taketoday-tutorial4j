package cn.tuyucheng.taketoday.autoconfiguration;

import cn.tuyucheng.taketoday.autoconfiguration.service.CustomService;
import cn.tuyucheng.taketoday.autoconfiguration.service.DefaultService;
import cn.tuyucheng.taketoday.autoconfiguration.service.SimpleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalOnPropertyIntegrationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	void whenGivenCustomPropertyValue_thenCustomServiceCreated() {
		this.contextRunner.withPropertyValues("cn.tuyucheng.taketoday.service=custom")
			.withUserConfiguration(SimpleServiceConfiguration.class)
			.run(context -> {
				assertThat(context).hasBean("customService");
				SimpleService simpleService = context.getBean(CustomService.class);
				assertThat(simpleService.serve()).isEqualTo("Custom Service");
				assertThat(context).doesNotHaveBean("defaultService");
			});
	}

	@Test
	void whenGivenDefaultPropertyValue_thenDefaultServiceCreated() {
		this.contextRunner.withPropertyValues("cn.tuyucheng.taketoday.service=default")
			.withUserConfiguration(SimpleServiceConfiguration.class)
			.run(context -> {
				assertThat(context).hasBean("defaultService");
				SimpleService simpleService = context.getBean(DefaultService.class);
				assertThat(simpleService.serve()).isEqualTo("Default Service");
				assertThat(context).doesNotHaveBean("customService");
			});
	}

	@Configuration
	@TestPropertySource("classpath:ConditionalOnPropertyTest.properties")
	protected static class SimpleServiceConfiguration {

		@Bean
		@ConditionalOnProperty(name = "cn.tuyucheng.taketoday.service", havingValue = "default")
		@ConditionalOnMissingBean
		public DefaultService defaultService() {
			return new DefaultService();
		}

		@Bean
		@ConditionalOnProperty(name = "cn.tuyucheng.taketoday.service", havingValue = "custom")
		@ConditionalOnMissingBean
		public CustomService customService() {
			return new CustomService();
		}
	}
}