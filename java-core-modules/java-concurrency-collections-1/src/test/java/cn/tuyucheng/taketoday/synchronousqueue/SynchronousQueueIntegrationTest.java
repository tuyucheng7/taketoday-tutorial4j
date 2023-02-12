package cn.tuyucheng.taketoday.synchronousqueue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
class SynchronousQueueIntegrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(SynchronousQueueIntegrationTest.class);

	@Test
	void givenTwoThreads_whenWantToExchangeUsingLockGuardedVariable_thenItSucceed() throws InterruptedException {
		// given
		ExecutorService executor = Executors.newFixedThreadPool(2);
		AtomicInteger sharedState = new AtomicInteger();
		CountDownLatch countDownLatch = new CountDownLatch(1);

		Runnable producer = () -> {
			int producedElement = ThreadLocalRandom.current().nextInt();
			LOG.info("Saving an element: " + producedElement + " to the exchange point");
			sharedState.set(producedElement);
			countDownLatch.countDown();
		};

		Runnable consumer = () -> {
			try {
				countDownLatch.await();
				int consumedElement = sharedState.get();
				LOG.info("consumed an element: " + consumedElement + " from the exchange point");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};

		// when
		executor.execute(producer);
		executor.execute(consumer);

		// then
		executor.awaitTermination(500, TimeUnit.MILLISECONDS);
		executor.shutdown();
		assertEquals(0, countDownLatch.getCount());
	}

	@Test
	void givenTwoThreads_whenWantToExchangeUsingSynchronousQueue_thenItSucceed() throws InterruptedException {
		// given
		ExecutorService executor = Executors.newFixedThreadPool(2);
		final SynchronousQueue<Integer> queue = new SynchronousQueue<>();

		Runnable producer = () -> {
			int producedElement = ThreadLocalRandom.current().nextInt();
			try {
				LOG.info("Saving an element: " + producedElement + " to the exchange point");
				queue.put(producedElement);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};

		Runnable consumer = () -> {
			try {
				Integer consumedElement = queue.take();
				LOG.info("consumed an element: " + consumedElement + " from the exchange point");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};

		// when
		executor.execute(producer);
		executor.execute(consumer);

		// then
		executor.awaitTermination(500, TimeUnit.MILLISECONDS);
		executor.shutdown();
		assertEquals(0, queue.size());
	}
}