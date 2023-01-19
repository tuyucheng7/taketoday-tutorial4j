package cn.tuyucheng.taketoday.fibonacci.impl;

import cn.tuyucheng.taketoday.fibonacci.FibonacciSequenceGenerator;
import cn.tuyucheng.taketoday.fibonacci.FibonacciSequenceGeneratorFixture;

/**
 * Unit test which reuses the {@link FibonacciSequenceGeneratorFixture} test mix-in exported from the fibonacci-spi project.
 */
final class RecursiveFibonacciUnitTest implements FibonacciSequenceGeneratorFixture {
	@Override
	public FibonacciSequenceGenerator provide() {
		return new RecursiveFibonacci();
	}
}