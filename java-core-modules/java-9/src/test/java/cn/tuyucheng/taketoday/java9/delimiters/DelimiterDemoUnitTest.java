package cn.tuyucheng.taketoday.java9.delimiters;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelimiterDemoUnitTest {

	@Test
	void givenSimpleCharacterDelimiter_whenScannerWithDelimiter_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiter, "Welcome to Tuyucheng", "\\s", Arrays.asList("Welcome", "to", "Tuyucheng"));
	}

	@Test
	void givenStringDelimiter_whenScannerWithDelimiter_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiter, "HelloTuyuchengHelloWorld", "Hello", Arrays.asList("Tuyucheng", "World"));
	}

	@Test
	void givenVariousPossibleDelimiters_whenScannerWithDelimiter_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiter, "Welcome to Tuyucheng.\nThank you for reading.\nThe team", "\n|\\s", Arrays.asList("Welcome", "to", "Tuyucheng.", "Thank", "you", "for", "reading.", "The", "team"));
	}

	@Test
	void givenWildcardRegexDelimiter_whenScannerWithDelimiter_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiter, "1aaaaaaa2aa3aaa4", "a+", Arrays.asList("1", "2", "3", "4"));
	}

	@Test
	void givenSimpleCharacterDelimiter_whenScannerWithDelimiterUsingPattern_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiterUsingPattern, "Welcome to Tuyucheng", Pattern.compile("\\s"), Arrays.asList("Welcome", "to", "Tuyucheng"));
	}

	@Test
	void givenStringDelimiter_whenScannerWithDelimiterUsingPattern_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiterUsingPattern, "HelloTuyuchengHelloWorld", Pattern.compile("Hello"), Arrays.asList("Tuyucheng", "World"));
	}

	@Test
	void givenVariousPossibleDelimiters_whenScannerWithDelimiterUsingPattern_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiterUsingPattern, "Welcome to Tuyucheng.\nThank you for reading.\nThe team", Pattern.compile("\n|\\s"), Arrays.asList("Welcome", "to", "Tuyucheng.", "Thank", "you", "for", "reading.", "The", "team"));
	}

	@Test
	void givenWildcardRegexDelimiters_whenScannerWithDelimiterUsingPattern_ThenInputIsCorrectlyParsed() {
		checkOutput(DelimiterDemo::scannerWithDelimiterUsingPattern, "1aaaaaaa2aa3aaa4", Pattern.compile("a*"), Arrays.asList("1", "2", "3", "4"));
	}

	void checkOutput(BiFunction<String, String, List<String>> function, String input, String delimiter, List<String> expectedOutput) {
		assertEquals(expectedOutput, function.apply(input, delimiter));
	}

	void checkOutput(BiFunction<String, Pattern, List<String>> function, String input, Pattern delimiter, List<String> expectedOutput) {
		assertEquals(expectedOutput, function.apply(input, delimiter));
	}

	@Test
	void whenBaseScanner_ThenWhitespacesAreUsedAsDelimiters() {
		assertEquals(List.of("Welcome", "at", "Tuyucheng"), DelimiterDemo.baseScanner("Welcome at Tuyucheng"));
	}
}