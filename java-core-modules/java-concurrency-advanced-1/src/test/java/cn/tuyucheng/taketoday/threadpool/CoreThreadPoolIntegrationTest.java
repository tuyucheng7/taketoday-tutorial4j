package cn.tuyucheng.taketoday.threadpool;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

class CoreThreadPoolIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(CoreThreadPoolIntegrationTest.class);

	@Test
	void whenCallingExecuteWithRunnable_thenRunnableIsExecuted() {
		CountDownLatch lock = new CountDownLatch(1);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			LOGGER.debug("Hello World");
			lock.countDown();
		});

		assertTimeout(Duration.ofMillis(1000), () -> lock.await(1000, TimeUnit.MILLISECONDS));
	}

	@Test
	void whenUsingExecutorServiceAndFuture_thenCanWaitOnFutureResult() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		Future<String> future = executorService.submit(() -> "Hello World");
		// some operations
		String result = future.get();

		assertEquals("Hello World", result);
	}

	@Test
	void whenUsingFixedThreadPool_thenCoreAndMaximumThreadsSizeAreTheSame() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

		executor.submit(() -> {
			Thread.sleep(1000);
			return null;
		});
		executor.submit(() -> {
			Thread.sleep(1000);
			return null;
		});
		executor.submit(() -> {
			Thread.sleep(1000);
			return null;
		});
		assertEquals(2, executor.getPoolSize());
		assertEquals(1, executor.getQueue().size());
	}

	@Test
	void whenUsingCachedThreadPool_thenPoolSizeGrowsUnbounded() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		executor.submit(() -> {
			Thread.sleep(1000);
			return null;
		});
		executor.submit(() -> {
			Thread.sleep(1000);
			return null;
		});
		executor.submit(() -> {
			Thread.sleep(1000);
			return null;
		});

		assertEquals(3, executor.getPoolSize());
		assertEquals(0, executor.getQueue().size());
	}

	@Test
	void whenUsingSingleThreadPool_thenTasksExecuteSequentially() {
		CountDownLatch lock = new CountDownLatch(2);
		AtomicInteger counter = new AtomicInteger();

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			counter.set(1);
			lock.countDown();
		});
		executor.submit(() -> {
			counter.compareAndSet(1, 2);
			lock.countDown();
		});

		assertTimeout(Duration.ofMillis(1000), () -> lock.await(1000, TimeUnit.MILLISECONDS));
		assertEquals(2, counter.get());
	}

	@Test
	void whenSchedulingTask_thenTaskExecutesWithinGivenPeriod() {
		CountDownLatch lock = new CountDownLatch(1);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
		executor.schedule(() -> {
			LOGGER.debug("Hello World");
			lock.countDown();
		}, 500, TimeUnit.MILLISECONDS);

		assertTimeout(Duration.ofMillis(1000), () -> lock.await(1000, TimeUnit.MILLISECONDS));
	}

	@Test
	void whenSchedulingTaskWithFixedPeriod_thenTaskExecutesMultipleTimes() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(3);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
		ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
			LOGGER.debug("Hello World");
			lock.countDown();
		}, 500, 100, TimeUnit.MILLISECONDS);

		lock.await();
		future.cancel(true);
	}

	@Test
	void whenUsingForkJoinPool_thenSumOfTreeElementsIsCalculatedCorrectly() {
		TreeNode tree = new TreeNode(5,
				new TreeNode(3),
				new TreeNode(2, new TreeNode(2), new TreeNode(8)));
		ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
		int sum = forkJoinPool.invoke(new CountingTask(tree));
		assertEquals(20, sum);
	}
}