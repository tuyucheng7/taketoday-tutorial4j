package cn.tuyucheng.taketoday.properties.lists;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringListPropertiesApplication.class)
class ListsPropertiesUnitTest {

	@Value("${arrayOfStrings}")
	private String[] arrayOfStrings;

	@Value("${arrayOfStrings}")
	private List<String> unexpectedListOfStrings;

	@Value("#{'${arrayOfStrings}'.split(',')}")
	private List<String> listOfStrings;

	@Value("#{${listOfStrings}}")
	private List<String> listOfStringsV2;

	@Value("#{'${listOfStringsWithCustomDelimiter}'.split(';')}")
	private List<String> listOfStringsWithCustomDelimiter;

	@Value("#{'${listOfBooleans}'.split(',')}")
	private List<Boolean> listOfBooleans;

	@Value("#{'${listOfIntegers}'.split(',')}")
	private List<Integer> listOfIntegers;

	@Value("#{'${listOfCharacters}'.split(',')}")
	private List<Character> listOfCharacters;

	@Autowired
	private Environment environment;

	@Test
	void whenContextIsInitialized_thenInjectedArrayContainsExpectedValues() {
		assertArrayEquals(new String[]{"Tuyucheng", "dot", "com"}, arrayOfStrings);
	}

	@Test
	void whenContextIsInitialized_thenInjectedListContainsUnexpectedValues() {
		assertEquals(Collections.singletonList("Tuyucheng,dot,com"), unexpectedListOfStrings);
	}

	@Test
	void whenContextIsInitialized_thenInjectedListContainsExpectedValues() {
		assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStrings);
	}

	@Test
	void whenContextIsInitialized_thenInjectedListV2ContainsExpectedValues() {
		assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStringsV2);
	}

	@Test
	void whenContextIsInitialized_thenInjectedListWithCustomDelimiterContainsExpectedValues() {
		assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStringsWithCustomDelimiter);
	}

	@Test
	void whenContextIsInitialized_thenInjectedListOfBasicTypesContainsExpectedValues() {
		assertEquals(Arrays.asList(false, false, true), listOfBooleans);
		assertEquals(Arrays.asList(1, 2, 3, 4), listOfIntegers);
		assertEquals(Arrays.asList('a', 'b', 'c'), listOfCharacters);
	}

	@Test
	void whenReadingFromSpringEnvironment_thenPropertiesHaveExpectedValues() {
		String[] arrayOfStrings = environment.getProperty("arrayOfStrings", String[].class);
		List<String> listOfStrings = (List<String>) environment.getProperty("arrayOfStrings", List.class);

		assertArrayEquals(new String[]{"Tuyucheng", "dot", "com"}, arrayOfStrings);
		assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStrings);
	}
}