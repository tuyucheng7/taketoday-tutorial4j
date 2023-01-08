package cn.tuyucheng.taketoday.intdivision;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegerDivisionUnitTest {
	@Test
	void givenTwoInt_whenExecDivision_shouldGetInteger() {
		int i = 10 / 4;
		assertEquals(2, i);

		float x = 10 / 4;
		assertEquals(2, x);
	}

	@Test
	void givenTwoInt_whenCastAnyoneToFloat_shouldGetFloatResult() {
		float x = (float) 10 / 4;
		assertEquals(2.5, x);

		float y = 10 / (float) 8;
		assertEquals(1.25, y);
	}

}
