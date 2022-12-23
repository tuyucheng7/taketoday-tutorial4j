package cn.tuyucheng.taketoday.annotations.conditional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Java8ConditionUnitTest {

	@Test
	void whenOnJava8_thenJava8ConditionShouldPass() {
		try (MockedStatic<ConditionalUtils> theMock = Mockito.mockStatic(ConditionalUtils.class)) {
			theMock.when(ConditionalUtils::isJava8)
				.thenReturn(true);
			assertTrue(
				new Java8Condition().matches(
					Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
				)
			);
		}
	}

	@Test
	void whenNotOnJava8_thenJava8ConditionShouldNotPass() {
		try (MockedStatic<ConditionalUtils> theMock = Mockito.mockStatic(ConditionalUtils.class)) {
			theMock.when(ConditionalUtils::isJava8)
				.thenReturn(false);
			assertFalse(
				new Java8Condition().matches(
					Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
				)
			);
		}
	}
}