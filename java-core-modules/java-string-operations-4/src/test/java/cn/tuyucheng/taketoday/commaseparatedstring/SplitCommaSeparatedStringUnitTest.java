package cn.tuyucheng.taketoday.commaseparatedstring;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static cn.tuyucheng.taketoday.commaseparatedstring.SplitCommaSeparatedString.splitMultiLineWithOpenCSV;
import static cn.tuyucheng.taketoday.commaseparatedstring.SplitCommaSeparatedString.splitWithGuava;
import static cn.tuyucheng.taketoday.commaseparatedstring.SplitCommaSeparatedString.splitWithParser;
import static cn.tuyucheng.taketoday.commaseparatedstring.SplitCommaSeparatedString.splitWithRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertArrayEquals;

public class SplitCommaSeparatedStringUnitTest {

	@Test
	public void givenSingleLineInput_whenParsing_shouldIgnoreCommasInsideDoubleQuotes() {
		String input = "baeldung,tutorial,splitting,text,\"ignoring this comma,\"";

		var matcher = contains("baeldung", "tutorial", "splitting", "text", "\"ignoring this comma,\"");
		assertThat(splitWithParser(input), matcher);
		assertThat(splitWithRegex(input), matcher);
		assertThat(splitWithGuava(input), matcher);
	}

	@Test
	public void givenMultiLineInput_whenParsing_shouldIgnoreCommasInsideDoubleQuotes() throws IOException {
		String input = "baeldung,tutorial,splitting,text,\"ignoring this comma,\"" + System.lineSeparator()
			+ "splitting,a,regular,line,no double quotes";

		String[] firstLine = new String[]{"baeldung", "tutorial", "splitting", "text", "ignoring this comma,"};
		String[] secondLine = new String[]{"splitting", "a", "regular", "line", "no double quotes"};

		List<String[]> result = splitMultiLineWithOpenCSV(input);

		assertThat(result, hasSize(2));
		assertArrayEquals(firstLine, result.get(0));
		assertArrayEquals(secondLine, result.get(1));
	}

}
