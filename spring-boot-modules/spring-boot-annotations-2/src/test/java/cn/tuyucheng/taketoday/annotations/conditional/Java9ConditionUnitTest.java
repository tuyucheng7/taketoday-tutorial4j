package cn.tuyucheng.taketoday.annotations.conditional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Java9ConditionUnitTest {

	@Test
	void whenOnJava9_thenJava9ConditionShouldPass() {
		try (MockedStatic<ConditionalUtils> theMock = Mockito.mockStatic(ConditionalUtils.class)) {
			theMock.when(ConditionalUtils::isJava9)
				.thenReturn(true);
			assertTrue(
				new Java9Condition().matches(
					Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
				)
			);
		}
	}

	@Test
	void whenNotOnJava9_thenJava9ConditionShouldNotPass() {
		try (MockedStatic<ConditionalUtils> theMock = Mockito.mockStatic(ConditionalUtils.class)) {
			theMock.when(ConditionalUtils::isJava9)
				.thenReturn(false);
			assertFalse(
				new Java9Condition().matches(
					Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
				)
			);
		}
	}
}