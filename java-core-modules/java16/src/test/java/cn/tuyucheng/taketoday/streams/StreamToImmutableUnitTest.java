package cn.tuyucheng.taketoday.streams;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class StreamToImmutableUnitTest {

	@Test
	void whenUsingStreamToList_thenReturnImmutableList() {
		List<String> immutableList = Stream.of("a", "b", "c", "d")
			.toList();

		Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableList.add("e"));
	}
}