package tuyuchenggreeter;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class FormatterTest {

	@Test
	public void testFormatter() {
		String dateRegex1 = "^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])$";
		String dateString = Formatter.getFormattedDate();
		assertTrue(Pattern.matches(dateRegex1, dateString));
	}
}