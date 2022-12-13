package cn.tuyucheng.taketoday.stream;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class SupplierStreamUnitTest {

	@Test
	void givenStream_whenStreamUsedTwice_thenThrowException() {
		Stream<String> stringStream = Stream.of("A", "B", "C", "D");
		Optional<String> result1 = stringStream.findAny();
		System.out.println(result1.get());
		assertThrows(IllegalStateException.class, () -> stringStream.findFirst().get());
	}

	@Test
	void givenStream_whenUsingSupplier_thenNoExceptionIsThrown() {
		try {
			Supplier<Stream<String>> streamSupplier = () -> Stream.of("A", "B", "C", "D");
			Optional<String> result1 = streamSupplier.get().findAny();
			System.out.println(result1.get());
			Optional<String> result2 = streamSupplier.get().findFirst();
			System.out.println(result2.get());
		} catch (IllegalStateException e) {
			fail();
		}
	}
}