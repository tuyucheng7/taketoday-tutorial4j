package cn.tuyucheng.taketoday.concurrent.future;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactorialSquareCalculatorUnitTest {

	@Test
	void whenCalculatesFactorialSquare_thenReturnCorrectValue() {
		ForkJoinPool forkJoinPool = new ForkJoinPool();

		FactorialSquareCalculator calculator = new FactorialSquareCalculator(10);
		forkJoinPool.execute(calculator);

		assertEquals(385, calculator.join().intValue(), "The sum of the squares from 1 to 10 is 385");
	}
}