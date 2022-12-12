package cn.tuyucheng.taketoday.stream;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfiniteStreamUnitTest {

	@Test
	void givenInfiniteStream_whenUseIntermediateLimitMethod_thenShouldTerminateInFiniteTime() {
		// given
		Stream<Integer> infiniteStream = Stream.iterate(0, i -> i + 2);

		// when
		List<Integer> collect = infiniteStream
			.limit(10)
			.collect(Collectors.toList());

		// then
		assertEquals(Arrays.asList(0, 2, 4, 6, 8, 10, 12, 14, 16, 18), collect);
	}

	@Test
	void givenInfiniteStreamOfRandomInts_whenUseLimit_shouldTerminateInFiniteTime() {
		// given
		Supplier<UUID> randomUUIDSupplier = UUID::randomUUID;
		Stream<UUID> infiniteStreamOfRandomUUID = Stream.generate(randomUUIDSupplier);

		// when
		List<UUID> randomInts = infiniteStreamOfRandomUUID
			.skip(10)
			.limit(10)
			.collect(Collectors.toList());

		// then
		assertEquals(10, randomInts.size());
	}
}