package cn.tuyucheng.taketoday.concurrent.semaphores;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SemaphoresManualTest {

	// ========= login queue ======

	@Test
	void givenLoginQueue_whenReachLimit_thenBlocked() throws InterruptedException {
		final int slots = 10;
		final ExecutorService executor = Executors.newFixedThreadPool(slots);
		final LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
		IntStream.range(0, slots)
				.forEach(user -> executor.execute(loginQueue::tryLogin));
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		assertEquals(0, loginQueue.availableSlots());
		assertFalse(loginQueue.tryLogin());
	}

	@Test
	void givenLoginQueue_whenLogout_thenSlotsAvailable() throws InterruptedException {
		final int slots = 10;
		final ExecutorService executor = Executors.newFixedThreadPool(slots);
		final LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
		IntStream.range(0, slots)
				.forEach(user -> executor.execute(loginQueue::tryLogin));

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		assertEquals(0, loginQueue.availableSlots());
		loginQueue.logout();
		assertTrue(loginQueue.availableSlots() > 0);
		assertTrue(loginQueue.tryLogin());
	}

	// ========= delay queue =======

	@Test
	void givenDelayQueue_whenReachLimit_thenBlocked() throws InterruptedException {
		final int slots = 50;
		final ExecutorService executor = Executors.newFixedThreadPool(slots);
		final DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
		IntStream.range(0, slots)
				.forEach(user -> executor.submit(delayQueue::tryAdd));
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		assertEquals(0, delayQueue.availableSlots());
		assertFalse(delayQueue.tryAdd());
	}

	@Test
	void givenDelayQueue_whenTimePass_thenSlotsAvailable() throws InterruptedException {
		final int slots = 50;
		final ExecutorService executor = Executors.newFixedThreadPool(slots);
		DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
		IntStream.range(0, slots)
				.forEach(user -> executor.submit(delayQueue::tryAdd));

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		assertEquals(0, delayQueue.availableSlots());
		TimeUnit.MILLISECONDS.sleep(1000);

		assertTrue(delayQueue.availableSlots() > 0);
		assertTrue(delayQueue.tryAdd());
	}

	// ========== mutex ========

	@Test
	void whenMutexAndMultipleThreads_thenBlocked() {
		final int count = 5;
		final ExecutorService executor = Executors.newFixedThreadPool(count);
		final CounterUsingMutex counter = new CounterUsingMutex();
		IntStream.range(0, count)
				.forEach(user -> executor.submit(() -> {
					try {
						counter.increase();
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}));
		executor.shutdown();

		assertTrue(counter.hasQueuedThreads());
	}

	@Test
	void givenMutexAndMultipleThreads_ThenDelay_thenCorrectCount() throws InterruptedException {
		final int count = 5;
		final ExecutorService executorService = Executors.newFixedThreadPool(count);
		final CounterUsingMutex counter = new CounterUsingMutex();
		IntStream.range(0, count)
				.forEach(user -> executorService.execute(() -> {
					try {
						counter.increase();
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}));

		executorService.shutdown();
		assertTrue(counter.hasQueuedThreads());
		Thread.sleep(5000);
		assertFalse(counter.hasQueuedThreads());
		assertEquals(count, counter.getCount());
	}
}