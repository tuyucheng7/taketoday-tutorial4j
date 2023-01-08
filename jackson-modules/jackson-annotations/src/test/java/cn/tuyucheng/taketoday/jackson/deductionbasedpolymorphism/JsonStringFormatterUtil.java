package cn.tuyucheng.taketoday.jackson.deductionbasedpolymorphism;

public class JsonStringFormatterUtil {

	public static String formatJson(String input) {
		return input.replaceAll("'", "\"");
	}

}
