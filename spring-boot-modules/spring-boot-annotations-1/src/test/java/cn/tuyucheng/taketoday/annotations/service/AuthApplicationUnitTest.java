package cn.tuyucheng.taketoday.annotations.service;

import cn.tuyucheng.taketoday.annotations.service.abstracts.AbstractAuthenticationService;
import cn.tuyucheng.taketoday.annotations.service.config.AbstractsAnnotatedTestConfiguration;
import cn.tuyucheng.taketoday.annotations.service.config.ConcreteClassesAnnotatedTestConfiguration;
import cn.tuyucheng.taketoday.annotations.service.config.InterfacesAnnotatedTestConfiguration;
import cn.tuyucheng.taketoday.annotations.service.interfaces.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthApplicationUnitTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	void whenOnlyInterfacesAnnotated_noSuchBeanDefinitionExceptionThrown() {
		contextRunner
			.withUserConfiguration(InterfacesAnnotatedTestConfiguration.class)
			.run(context -> assertThrows(NoSuchBeanDefinitionException.class,
				() -> context.getBean(AuthenticationService.class)));
	}

	@Test
	void whenOnlyAbstractClassesAnnotated_noSuchBeanDefinitionExceptionThrown() {
		contextRunner
			.withUserConfiguration(AbstractsAnnotatedTestConfiguration.class)
			.run(context -> assertThrows(NoSuchBeanDefinitionException.class,
				() -> context.getBean(AbstractAuthenticationService.class)));
	}

	@Test
	void whenConcreteClassesAnnotated_noExceptionThrown() {
		contextRunner
			.withUserConfiguration(ConcreteClassesAnnotatedTestConfiguration.class)
			.run(context -> {
				AuthenticationService inMemoryAuthService = context.getBean(AuthenticationService.class);
				AbstractAuthenticationService ldapAuthService = context.getBean(AbstractAuthenticationService.class);

				assertNotNull(inMemoryAuthService);
				assertNotNull(ldapAuthService);
			});
	}
}