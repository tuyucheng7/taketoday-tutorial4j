package cn.tuyucheng.taketoday.switchpatterns;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.switchpatterns.ParenthesizedPatterns.getDoubleValueUsingIf;
import static cn.tuyucheng.taketoday.switchpatterns.ParenthesizedPatterns.getDoubleValueUsingParenthesizedPatterns;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParenthesizedPatternsUnitTest {

	@Test
	void givenIfImplementation_whenUsingEmptyString_thenDoubleIsReturned() {
		assertEquals(0d, getDoubleValueUsingIf(""));
	}

	@Test
	void givenIfImplementation_whenUsingNonEmptyString_thenDoubleIsReturned() {
		assertEquals(10d, getDoubleValueUsingIf("10"));
	}

	@Test
	void givenIfImplementation_whenStringContainsSpecialChar_thenDoubleIsReturned() {
		assertEquals(0d, getDoubleValueUsingIf("@10"));
	}

	@Test
	void givenPatternsImplementation_whenUsingEmptyString_thenDoubleIsReturned() {
		assertEquals(0d, getDoubleValueUsingParenthesizedPatterns(""));
	}

	@Test
	void givenPatternsImplementation_whenUsingNonEmptyString_thenDoubleIsReturned() {
		assertEquals(10d, getDoubleValueUsingParenthesizedPatterns("10"));
	}

	@Test
	void givenPatternsImplementation_whenStringContainsSpecialChar_thenDoubleIsReturned() {
		assertEquals(0d, getDoubleValueUsingParenthesizedPatterns("@10"));
	}
}