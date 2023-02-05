package cn.tuyucheng.taketoday.java10;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Java10FeaturesUnitTest {

	private List<Integer> someIntList;

	@BeforeEach
	void setup() {
		someIntList = new ArrayList<>();

		someIntList.add(1);
		someIntList.add(2);
		someIntList.add(3);
	}

	@Test
	void whenVarInitWithString_thenGetStringTypeVar() {
		var message = "Hello, Java 10";
		var hello = "Spring";
		System.out.println(hello);
		assertTrue(message instanceof String);
	}

	@Test
	void whenVarInitWithAnonymous_thenGetAnonymousType() {
		var obj = new Object() {
		};
		assertNotEquals(obj.getClass(), Object.class);
	}

	@Test
	void whenModifyCopyOfList_thenThrowsException() {
		List<Integer> copyList = List.copyOf(someIntList);
		assertThrows(UnsupportedOperationException.class, () -> copyList.add(4));
	}

	@Test
	void whenModifyToUnmodifiableList_thenThrowsException() {
		List<Integer> evenList = someIntList.stream()
			.filter(i -> i % 2 == 0)
			.collect(Collectors.toUnmodifiableList());
		assertThrows(UnsupportedOperationException.class, () -> evenList.add(4));
	}

	@Test
	void whenListContainsInteger_OrElseThrowReturnsInteger() {
		Integer firstEven = someIntList.stream()
			.filter(i -> i % 2 == 0)
			.findFirst()
			.orElseThrow();
		is(firstEven).equals(2);
	}
}