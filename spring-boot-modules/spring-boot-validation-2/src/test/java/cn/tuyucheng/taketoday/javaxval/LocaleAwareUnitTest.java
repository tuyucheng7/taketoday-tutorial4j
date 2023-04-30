package cn.tuyucheng.taketoday.javaxval;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Locale;

public abstract class LocaleAwareUnitTest {
	private static Locale previousDefault;

	@BeforeClass
	public static void setupLocale() {
		previousDefault = Locale.getDefault();

		Locale.setDefault(Locale.US);
	}

	@AfterClass
	public static void resetLocale() {
		Locale.setDefault(previousDefault);
	}

}
