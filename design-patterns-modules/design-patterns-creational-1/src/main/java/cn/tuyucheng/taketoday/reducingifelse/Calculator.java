package cn.tuyucheng.taketoday.reducingifelse;

public class Calculator {

	public int calculate(int a, int b, String operator) {
		int result = Integer.MIN_VALUE;

		if ("add".equals(operator)) {
			result = a + b;
		} else if ("multiply".equals(operator)) {
			result = a * b;
		} else if ("divide".equals(operator)) {
			result = a / b;
		} else if ("subtract".equals(operator)) {
			result = a - b;
		} else if ("modulo".equals(operator)) {
			result = a % b;
		}
		return result;
	}

	public int calculateUsingSwitch(int a, int b, String operator) {
		return switch (operator) {
			case "add" -> a + b;
			case "multiply" -> a * b;
			case "divide" -> a / b;
			case "subtract" -> a - b;
			case "modulo" -> a % b;
			default -> Integer.MIN_VALUE;
		};
	}

	public int calculateUsingSwitch(int a, int b, Operator operator) {
		return switch (operator) {
			case ADD -> a + b;
			case MULTIPLY -> a * b;
			case DIVIDE -> a / b;
			case SUBTRACT -> a - b;
			case MODULO -> a % b;
			default -> Integer.MIN_VALUE;
		};
	}

	public int calculate(int a, int b, Operator operator) {
		return operator.apply(a, b);
	}

	public int calculateUsingFactory(int a, int b, String operation) {
		Operation targetOperation = OperatorFactory
			.getOperation(operation)
			.orElseThrow(() -> new IllegalArgumentException("Invalid Operator"));
		return targetOperation.apply(a, b);
	}

	public int calculate(Command command) {
		return command.execute();
	}
}