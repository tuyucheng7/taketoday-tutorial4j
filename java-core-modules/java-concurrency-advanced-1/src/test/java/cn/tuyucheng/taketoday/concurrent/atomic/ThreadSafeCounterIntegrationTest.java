package cn.tuyucheng.taketoday.concurrent.atomic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadSafeCounterIntegrationTest {

	@Test
	void givenMultiThread_whenSafeCounterWithLockIncrement() throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(3);
		SafeCounterWithLock safeCounter = new SafeCounterWithLock();

		IntStream.range(0, 1000).forEach(count -> service.submit(safeCounter::increment));
		service.awaitTermination(100, TimeUnit.MILLISECONDS);

		assertEquals(1000, safeCounter.getValue());
	}

	@Test
	void givenMultiThread_whenSafeCounterWithoutLockIncrement() throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(3);
		SafeCounterWithoutLock safeCounter = new SafeCounterWithoutLock();

		IntStream.range(0, 1000).forEach(count -> service.submit(safeCounter::increment));
		service.awaitTermination(100, TimeUnit.MILLISECONDS);

		assertEquals(1000, safeCounter.getValue());
	}
}