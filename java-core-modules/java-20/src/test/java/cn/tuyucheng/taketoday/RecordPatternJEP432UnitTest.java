package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordPatternJEP432UnitTest {

	record Person(String name, int age) {
	}

	record Box<T>(T t) {
	}

	record Pair(Object fst, Object snd) {
	}

	@Test
	void givenListOfPersons_whenUsingPatternMatching_thenCorrect() {
		// Create a list of persons
		var persons = List.of(
			new Person("John", 20),
			new Person("Mary", 25),
			new Person("Peter", 30));
		for (Person(var name, var age) : persons) {
			assertThat(age).isGreaterThan(18);
		}
	}

	@Test
	void givenGenericBox_whenUsingPatternMatching_thenCorrect() {
		// Create a generic box
		var box = new Box<>(10);
		if (box instanceof Box(var b)) {
			assertTrue(b > 5);
		}

		assertThat(box).isInstanceOf(Box.class);
	}

	@Test
	void givenPairsContainsNull_whenUsingPatternMatching_thenShouldThrowMatchException() {
		// Create Pair array contains null element
		var ps = new Pair[]{
			new Pair("John", 20),
			null,
			new Pair("Peter", 30)};

		assertThrows(MatchException.class, () -> {
			for (Pair(var f, var s) : ps) {
			}
		});
	}
}