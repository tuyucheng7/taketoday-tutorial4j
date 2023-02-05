package cn.tuyucheng.taketoday.java10.streams;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

class StreamToImmutableJava10UnitTest {

	@Test
	void whenUsingCollectorsToUnmodifiableList_thenSuccess() {
		List<String> givenList = Arrays.asList("a", "b", "c");
		List<String> result = givenList.stream()
			.collect(toUnmodifiableList());

		System.out.println(result.getClass());
	}
}