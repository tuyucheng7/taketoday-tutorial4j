package cn.tuyucheng.taketoday.positivenegative;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.positivenegative.PositiveOrNegative.Result.NEGATIVE;
import static cn.tuyucheng.taketoday.positivenegative.PositiveOrNegative.Result.POSITIVE;
import static cn.tuyucheng.taketoday.positivenegative.PositiveOrNegative.Result.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PositiveOrNegativeUnitTest {
	@Test
	void givenIntegers_whenChkPositiveOrNegativeByOperator_thenReturnExpectedResult() {
		assertEquals(POSITIVE, PositiveOrNegative.byOperator(42));
		assertEquals(ZERO, PositiveOrNegative.byOperator(0));
		assertEquals(NEGATIVE, PositiveOrNegative.byOperator(-700));
	}

	@Test
	void givenIntegers_whenChkPositiveOrNegativeBySignum_thenReturnExpectedResult() {
		assertEquals(POSITIVE, PositiveOrNegative.bySignum(42));
		assertEquals(ZERO, PositiveOrNegative.bySignum(0));
		assertEquals(NEGATIVE, PositiveOrNegative.bySignum(-700));
	}

	@Test
	void givenFloats_whenChkPositiveOrNegativeBySignum_thenReturnExpectedResult() {
		assertEquals(POSITIVE, PositiveOrNegative.bySignum(4.2f));
		assertEquals(ZERO, PositiveOrNegative.bySignum(0f));
		assertEquals(NEGATIVE, PositiveOrNegative.bySignum(-7.7f));
	}
}
