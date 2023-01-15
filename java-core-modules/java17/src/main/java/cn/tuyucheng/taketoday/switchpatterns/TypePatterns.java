package cn.tuyucheng.taketoday.switchpatterns;

public class TypePatterns {

	static double getDoubleUsingIf(Object o) {
		double result;

		if (o instanceof Integer) {
			result = ((Integer) o).doubleValue();
		} else if (o instanceof Float) {
			result = ((Float) o).doubleValue();
		} else if (o instanceof String) {
			result = Double.parseDouble(((String) o));
		} else {
			result = 0d;
		}

		return result;
	}

	static Double getDoubleUsingSwitch(Object o) {
		return switch (o) {
			case Integer i -> i.doubleValue();
			case Float f -> f.doubleValue();
			case String s -> Double.parseDouble(s);
			default -> 0d;
		};
	}
}