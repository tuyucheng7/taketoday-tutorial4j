package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PatternMatchingJEP433UnitTest {

	record OneDimensionalPoint(int x) {
		public int x() {
			return x / 0;
		}
	}

	record Wrapper<T>(T t) {
	}

	@Test
	void whenUsingPatternWithGenericObj_thenShouldInferType() {
		var w = new Wrapper<String>("some text");

		switch (w) {
			case Wrapper(var v) -> assertEquals("some text", v);
		}
	}

	@Test
	void whenUsingPatternAndErrorHasOccurred_thenShouldThrowsMatchException() {
		var dot = new OneDimensionalPoint(10);
		assertThrows(MatchException.class, () -> {
			switch (dot) {
				case OneDimensionalPoint(var x) -> System.out.println("1D ponit");
			}
		});
	}
}