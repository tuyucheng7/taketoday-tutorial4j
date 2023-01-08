package cn.tuyucheng.taketoday.isnumeric;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class RegularExpressionsUnitTest {
	private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum)
			.matches();
	}

	@Test
	public void whenUsingRegularExpressions_thenTrue() {
		// Valid Numbers
		assertThat(isNumeric("22")).isTrue();
		assertThat(isNumeric("5.05")).isTrue();
		assertThat(isNumeric("-200")).isTrue();

		// Invalid Numbers
		assertThat(isNumeric(null)).isFalse();
		assertThat(isNumeric("abc")).isFalse();
	}
}
