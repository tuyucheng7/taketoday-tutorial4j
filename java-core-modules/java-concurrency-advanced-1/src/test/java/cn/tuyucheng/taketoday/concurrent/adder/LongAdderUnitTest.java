package cn.tuyucheng.taketoday.concurrent.adder;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import static com.jayway.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LongAdderUnitTest {

	@Test
	void givenMultipleThread_whenTheyWriteToSharedLongAdder_thenShouldCalculateSumForThem() throws InterruptedException {
		LongAdder counter = new LongAdder();
		ExecutorService executorService = Executors.newFixedThreadPool(8);

		int numberOfThreads = 4;
		int numberOfIncrements = 100;

		Runnable incrementAction = () -> IntStream
				.range(0, numberOfIncrements)
				.forEach(x -> counter.increment());

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(incrementAction);
		}

		executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
		executorService.shutdown();

		assertEquals(counter.sum(), numberOfIncrements * numberOfThreads);
		assertEquals(counter.sum(), numberOfIncrements * numberOfThreads);
	}

	@Test
	void givenMultipleThread_whenTheyWriteToSharedLongAdder_thenShouldCalculateSumForThemAndResetAdderAfterward() throws InterruptedException {
		LongAdder counter = new LongAdder();
		ExecutorService executorService = Executors.newFixedThreadPool(8);

		int numberOfThreads = 4;
		int numberOfIncrements = 100;

		Runnable incrementAction = () -> IntStream
				.range(0, numberOfIncrements)
				.forEach(i -> counter.increment());

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.execute(incrementAction);
		}

		executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
		executorService.shutdown();

		assertEquals(counter.sumThenReset(), numberOfIncrements * numberOfThreads);
		await().until(() -> assertEquals(counter.sum(), 0));
	}
}