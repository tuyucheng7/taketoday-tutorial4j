package cn.tuyucheng.taketoday.streams.breakforeach;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BreakFromStreamForEachUnitTest {

	@Test
	void whenCustomTakeWhileIsCalled_ThenCorrectItemsAreReturned() {
		Stream<String> initialStream = Stream.of("cat", "dog", "elephant", "fox", "rabbit", "duck");

		List<String> result = CustomTakeWhile.takeWhile(initialStream, x -> x.length() % 2 != 0)
				.collect(Collectors.toList());

		assertEquals(asList("cat", "dog"), result);
	}

	@Test
	void whenCustomForEachIsCalled_ThenCorrectItemsAreReturned() {
		Stream<String> initialStream = Stream.of("cat", "dog", "elephant", "fox", "rabbit", "duck");
		List<String> result = new ArrayList<>();

		CustomForEach.forEach(initialStream, (elem, breaker) -> {
			if (elem.length() % 2 == 0) {
				breaker.stop();
			} else {
				result.add(elem);
			}
		});

		assertEquals(asList("cat", "dog"), result);
	}
}