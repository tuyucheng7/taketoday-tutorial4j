package cn.tuyucheng.taketoday.annotations.conditional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsDevEnvConditionUnitTest {

	@Test
	void whenDevEnvEnabled_thenDevEnvConditionShouldPass() {
		System.setProperty("env", "dev");

		assertTrue(
			new IsDevEnvCondition().matches(
				Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
			)
		);
	}

	@Test
	void whenDevEnvNotEnabled_thenDevEnvConditionShouldNotPass() {
		System.setProperty("env", "not-dev");

		assertFalse(
			new IsDevEnvCondition().matches(
				Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
			)
		);
	}
}