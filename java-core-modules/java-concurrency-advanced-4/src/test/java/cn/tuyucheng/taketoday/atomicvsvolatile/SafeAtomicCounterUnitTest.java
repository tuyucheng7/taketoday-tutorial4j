package cn.tuyucheng.taketoday.atomicvsvolatile;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeAtomicCounterUnitTest {

	private static final int INCREMENT_COUNTER = 1000;
	private static final int TIMEOUT = 100;
	private static final int POOL_SIZE = 3;

	@Test
	void givenMultiThread_whenSafeAtomicCounterIncrement() throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE);
		SafeAtomicCounter safeCounter = new SafeAtomicCounter();
		IntStream.range(0, INCREMENT_COUNTER).forEach(count -> service.submit(safeCounter::increment));
		service.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
		assertEquals(INCREMENT_COUNTER, safeCounter.getValue());
	}
}