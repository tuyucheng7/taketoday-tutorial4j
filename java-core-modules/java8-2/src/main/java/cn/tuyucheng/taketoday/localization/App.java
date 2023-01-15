package cn.tuyucheng.taketoday.localization;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class App {

	public static void main(String[] args) {
		List<Locale> locales = Arrays.asList(Locale.UK, Locale.ITALY, Locale.FRANCE, Locale.forLanguageTag("pl-PL"));
		Localization.run(locales);
		JavaSEFormat.run(locales);
		ICUFormat.run(locales);
	}
}