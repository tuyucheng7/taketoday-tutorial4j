package cn.tuyucheng.taketoday.switchExpression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwitchUnitTest {

	@Test
	void switchJava12() {
		var month = Month.AUG;

		var value = switch (month) {
			case JAN, JUN, JUL -> 3;
			case FEB, SEP, OCT, NOV, DEC -> 1;
			case MAR, MAY, APR, AUG -> 2;
		};

		assertEquals(2, value);
	}

	@Test
	void switchLocalVariable() {
		var month = Month.AUG;
		int i = switch (month) {
			case JAN, JUN, JUL -> 3;
			case FEB, SEP, OCT, NOV, DEC -> 1;
			case MAR, MAY, APR, AUG -> month.toString().length() * 4;
			// int j = month.toString().length() * 4;
			// break j;
		};
		assertEquals(12, i);
	}

	enum Month {JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC}
}