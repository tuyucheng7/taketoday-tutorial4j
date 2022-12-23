package cn.tuyucheng.taketoday.annotations.conditional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsWindowsConditionUnitTest {

	@Test
	void whenOnWindows_thenIsWindowsConditionShouldPass() {
		try (MockedStatic<ConditionalUtils> theMock = Mockito.mockStatic(ConditionalUtils.class)) {
			theMock.when(ConditionalUtils::isWindows)
				.thenReturn(true);
			assertTrue(
				new IsWindowsCondition().matches(
					Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
				)
			);
		}
	}

	@Test
	void whenNotOnWindows_thenIsWindowsConditionShouldNotPass() {
		try (MockedStatic<ConditionalUtils> theMock = Mockito.mockStatic(ConditionalUtils.class)) {
			theMock.when(ConditionalUtils::isWindows)
				.thenReturn(false);
			assertFalse(
				new IsWindowsCondition().matches(
					Mockito.mock(ConditionContext.class), Mockito.mock(AnnotatedTypeMetadata.class)
				)
			);
		}
	}
}