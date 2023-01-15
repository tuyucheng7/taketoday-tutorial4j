package cn.tuyucheng.taketoday.java9;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionalToStreamUnitTest {

	@Test
	void testOptionalToStream() {
		Optional<String> op = Optional.ofNullable("String value");
		Stream<String> strOptionalStream = op.stream();
		Stream<String> filteredStream = strOptionalStream.filter((str) -> str != null && str.startsWith("String"));
		assertEquals(1, filteredStream.count());
	}
}