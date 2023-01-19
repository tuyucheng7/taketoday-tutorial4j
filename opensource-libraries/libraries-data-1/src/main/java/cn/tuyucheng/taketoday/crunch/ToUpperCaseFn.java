package cn.tuyucheng.taketoday.crunch;

import org.apache.crunch.MapFn;

public class ToUpperCaseFn extends MapFn<String, String> {

	@Override
	public String map(String input) {
		return input != null ? input.toUpperCase() : input;
	}
}
