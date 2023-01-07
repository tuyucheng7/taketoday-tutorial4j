package cn.tuyucheng.taketoday.constantspatterns;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantPatternUnitTest {
	@Test
	public void givenTwoNumbersAndAdd_whenCallingCalculatorOperatOneTwoNumbers_thenCorrectAnswerReturned() {
		Calculator calculator = new Calculator();
		double expected = 4;
		double answer = calculator.operateOnTwoNumbers(2, 2, Calculator.Operation.ADD);
		assertEquals(expected, answer);
	}

	@Test
	public void givenTwoNumbersAndAdd_whenCallingGeometryCalculatorOperatOneTwoNumbers_thenCorrectAnswerReturned() {
		GeometryCalculator calculator = new GeometryCalculator();
		double expected = 4;
		double answer = calculator.operateOnTwoNumbers(2, 2, GeometryCalculator.Operation.ADD);
		assertEquals(expected, answer);
	}
}
