package cn.tuyucheng.taketoday.annotations;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeApplicationUnitTest {
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withUserConfiguration(EmployeeApplication.class);

	@Test
	void whenApplicationContextRuns_thenContainAllDefinedBeans() {
		contextRunner.run(context -> assertAll(
			() -> assertTrue(context.containsBeanDefinition("employee")),
			() -> assertTrue(context.containsBeanDefinition("seniorEmployee")),
			() -> assertTrue(context.containsBeanDefinition("doctor")),
			() -> assertTrue(context.containsBeanDefinition("hospital")),
			() -> assertFalse(context.containsBeanDefinition("student")),
			() -> assertTrue(context.containsBeanDefinition("teacher"))));
	}
}