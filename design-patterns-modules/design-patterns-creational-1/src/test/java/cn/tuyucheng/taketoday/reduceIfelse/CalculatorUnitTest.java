package cn.tuyucheng.taketoday.reduceIfelse;

import cn.tuyucheng.taketoday.reducingifelse.AddCommand;
import cn.tuyucheng.taketoday.reducingifelse.Calculator;
import cn.tuyucheng.taketoday.reducingifelse.Operator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorUnitTest {

	@Test
	void whenCalculateAddUsingStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(3, 4, "add");
		assertEquals(7, result);
	}

	@Test
	void whenCalculateMultiplyUsingStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(3, 4, "multiply");
		assertEquals(12, result);
	}

	@Test
	void whenCalculateDivideUsingStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(12, 4, "divide");
		assertEquals(3, result);
	}

	@Test
	void whenCalculateSubtractUsingStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(7, 4, "subtract");
		assertEquals(3, result);
	}

	@Test
	void whenCalculateAddUsingSwitchStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingSwitch(3, 4, "add");
		assertEquals(7, result);
	}

	@Test
	void whenCalculateMultiplyUsingSwitchStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingSwitch(3, 4, "multiply");
		assertEquals(12, result);
	}

	@Test
	void whenCalculateDivideUsingSwitchStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingSwitch(12, 4, "divide");
		assertEquals(3, result);
	}

	@Test
	void whenCalculateSubtractUsingSwitchStringOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingSwitch(7, 4, "subtract");
		assertEquals(3, result);
	}

	@Test
	void whenCalculateAddUsingEnumOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(3, 4, Operator.valueOf("ADD"));
		assertEquals(7, result);
	}

	@Test
	void whenCalculateMultiplyUsingEnumOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(3, 4, Operator.valueOf("MULTIPLY"));
		assertEquals(12, result);
	}

	@Test
	void whenCalculateDivideUsingEnumOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(12, 4, Operator.valueOf("DIVIDE"));
		assertEquals(3, result);
	}

	@Test
	void whenCalculateSubtractUsingEnumOperator_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(7, 4, Operator.valueOf("SUBTRACT"));
		assertEquals(3, result);
	}

	@Test
	void whenCalculateUsingCommand_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculate(new AddCommand(3, 7));
		assertEquals(10, result);
	}

	@Test
	void whenCalculateAddUsingFactory_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingFactory(3, 4, "add");
		assertEquals(7, result);
	}

	@Test
	void whenCalculateMultiplyUsingFactory_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingFactory(3, 4, "multiply");
		assertEquals(0, result);
	}

	@Test
	void whenCalculateDivideUsingFactory_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingFactory(12, 4, "divide");
		assertEquals(3, result);
	}

	@Test
	void whenCalculateSubtractUsingFactory_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingFactory(7, 4, "subtract");
		assertEquals(3, result);
	}

	@Test
	void whenCalculateModuloUsingFactory_thenReturnCorrectResult() {
		Calculator calculator = new Calculator();
		int result = calculator.calculateUsingFactory(3, 4, "modulo");
		assertEquals(3, result);
	}
}