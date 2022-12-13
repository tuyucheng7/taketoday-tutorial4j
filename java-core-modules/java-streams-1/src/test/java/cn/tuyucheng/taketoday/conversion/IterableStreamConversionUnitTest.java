package cn.tuyucheng.taketoday.conversion;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IterableStreamConversionUnitTest {

	@Test
	void givenIterable_whenConvertedToStream_thenNotNull() {
		Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");

		assertNotNull(StreamSupport.stream(iterable.spliterator(), false));
	}

	@Test
	void whenConvertedToList_thenCorrect() {
		Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");

		List<String> result = StreamSupport.stream(iterable.spliterator(), false)
			.map(String::toUpperCase)
			.collect(Collectors.toList());

		assertThat(result, contains("TESTING", "ITERABLE", "CONVERSION", "TO", "STREAM"));
	}
}