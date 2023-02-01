package cn.tuyucheng.taketoday.switchpatterns;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.switchpatterns.GuardedPatterns.getDoubleValueUsingGuardedPatterns;
import static cn.tuyucheng.taketoday.switchpatterns.GuardedPatterns.getDoubleValueUsingIf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GuardedPatternsUnitTest {

	@Test
	void givenIfImplementation_whenUsingEmptyString_thenDoubleIsReturned() {
		assertEquals(0d, getDoubleValueUsingIf(""));
	}

	@Test
	void givenIfImplementation_whenUsingNonEmptyString_thenDoubleIsReturned() {
		assertEquals(10d, getDoubleValueUsingIf("10"));
	}

	@Test
	void givenPatternsImplementation_whenUsingEmptyString_thenDoubleIsReturned() {
		assertEquals(0d, getDoubleValueUsingGuardedPatterns(""));
	}

	@Test
	void givenPatternsImplementation_whenUsingNonEmptyString_thenDoubleIsReturned() {
		assertEquals(10d, getDoubleValueUsingGuardedPatterns("10"));
	}
}