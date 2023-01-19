package cn.tuyucheng.taketoday.concurrent.countdownlatch;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CountdownLatchExampleIntegrationTest {

	@Test
	void whenParallelProcessing_thenMainThreadWillBlockUntilCompletion() throws InterruptedException {
		List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch countDownLatch = new CountDownLatch(5);
		List<Thread> workers = Stream.generate(() -> new Thread(new Worker(outputScraper, countDownLatch)))
				.limit(5)
				.toList();

		workers.forEach(Thread::start);
		countDownLatch.await(); // Block until workers finish
		outputScraper.add("Latch released");

		assertThat(outputScraper).containsExactly("Counted down", "Counted down", "Counted down",
				"Counted down", "Counted down", "Latch released");
	}

	@Test
	void whenFailingToParallelProcess_thenMainThreadShouldTimeout() throws InterruptedException {
		List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch countDownLatch = new CountDownLatch(5);
		List<Thread> workers = Stream.generate(() -> new Thread(new BrokenWorker(outputScraper, countDownLatch)))
				.limit(5)
				.toList();

		workers.forEach(Thread::start);
		final boolean result = countDownLatch.await(3L, TimeUnit.SECONDS);

		assertThat(result).isFalse();
	}

	@Test
	void whenDoingLotsOfThreadsInParallel_thenStartThemAtTheSameTime() throws InterruptedException {
		List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch readyThreadCounter = new CountDownLatch(5);
		CountDownLatch callingThreadBlocker = new CountDownLatch(1);
		CountDownLatch completedThreadCounter = new CountDownLatch(5);
		List<Thread> workers = Stream.generate(() -> new Thread(new WaitingWorker(outputScraper, readyThreadCounter, callingThreadBlocker, completedThreadCounter)))
				.limit(5)
				.toList();

		workers.forEach(Thread::start);
		readyThreadCounter.await(); // Block until workers start
		outputScraper.add("Workers ready");
		callingThreadBlocker.countDown();
		completedThreadCounter.await(); // Block until workers finish
		outputScraper.add("Workers complete");

		assertThat(outputScraper).containsExactly("Workers ready", "Counted down", "Counted down", "Counted down",
				"Counted down", "Counted down", "Workers complete");
	}
}