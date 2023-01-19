package cn.tuyucheng.taketoday.concurrent.callable;

import java.util.concurrent.Callable;

public class FactorialTask implements Callable<Integer> {
	int number;

	public FactorialTask(int number) {
		this.number = number;
	}

	public Integer call() throws InvalidParameterException {
		int fact = 1;
		if (number < 0)
			throw new InvalidParameterException("Number must be positive");

		for (int count = number; count > 1; count--)
			fact = fact * count;

		return fact;
	}

	private static class InvalidParameterException extends Exception {
		public InvalidParameterException(String message) {
			super(message);
		}
	}
}