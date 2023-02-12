package cn.tuyucheng.taketoday.rejection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaturationPolicyUnitTest {
	private ThreadPoolExecutor executor;

	@AfterEach
	void shutdownExecutor() {
		if (executor != null && !executor.isTerminated()) {
			executor.shutdownNow();
		}
	}

	@Test
	void givenAbortPolicy_whenSaturated_thenShouldThrowRejectedExecutionException() {
		executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new AbortPolicy());
		executor.execute(() -> waitFor(250));

		assertThatThrownBy(() -> executor.execute(() -> System.out.println("Will be rejected"))).isInstanceOf(RejectedExecutionException.class);
	}

	@Test
	void givenCallerRunsPolicy_whenSaturated_thenTheCallerThreadRunsTheTask() {
		executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new CallerRunsPolicy());
		executor.execute(() -> waitFor(250));

		long startTime = System.currentTimeMillis();
		executor.execute(() -> waitFor(500));
		long blockedDuration = System.currentTimeMillis() - startTime;

		assertThat(blockedDuration).isGreaterThanOrEqualTo(500);
	}

	@Test
	void givenDiscardPolicy_whenSaturated_thenExecutorDiscardsTheNewTask() throws InterruptedException {
		executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new DiscardPolicy());
		executor.execute(() -> waitFor(100));

		BlockingQueue<String> queue = new LinkedBlockingDeque<>();
		executor.execute(() -> queue.offer("Result"));

		assertThat(queue.poll(200, MILLISECONDS)).isNull();
	}

	@Test
	void givenDiscardOldestPolicy_whenSaturated_thenExecutorDiscardsTheOldestTask() {
		executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new ArrayBlockingQueue<>(2), new DiscardOldestPolicy());
		executor.execute(() -> waitFor(100));

		BlockingQueue<String> queue = new LinkedBlockingDeque<>();
		executor.execute(() -> queue.offer("First"));
		executor.execute(() -> queue.offer("Second"));
		executor.execute(() -> queue.offer("Third"));

		waitFor(150);
		List<String> results = new ArrayList<>();
		queue.drainTo(results);
		assertThat(results).containsExactlyInAnyOrder("Second", "Third");
	}

	@Test
	void givenGrowPolicy_whenSaturated_thenExecutorIncreaseTheMaxPoolSize() {
		executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new ArrayBlockingQueue<>(2), new GrowPolicy());
		executor.execute(() -> waitFor(100));

		BlockingQueue<String> queue = new LinkedBlockingDeque<>();
		executor.execute(() -> queue.offer("First"));
		executor.execute(() -> queue.offer("Second"));
		executor.execute(() -> queue.offer("Third"));

		waitFor(150);
		List<String> results = new ArrayList<>();
		queue.drainTo(results);
		assertThat(results).containsExactlyInAnyOrder("First", "Second", "Third");
	}

	@Test
	void givenExecutorIsTerminated_whenSubmittedNewTask_thenTheSaturationPolicyApplies() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new LinkedBlockingQueue<>());
		executor.shutdownNow();

		assertThatThrownBy(() -> executor.execute(() -> {
		}))
			.isInstanceOf(RejectedExecutionException.class);
	}

	@Test
	void givenExecutorIsTerminating_whenSubmittedNewTask_thenTheSaturationPolicyApplies() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new LinkedBlockingQueue<>());
		executor.execute(() -> waitFor(100));
		executor.shutdown();

		assertThatThrownBy(() -> executor.execute(() -> {
		}))
			.isInstanceOf(RejectedExecutionException.class);
	}

	private static class GrowPolicy implements RejectedExecutionHandler {
		private final Lock lock = new ReentrantLock();

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			lock.lock();
			try {
				executor.setMaximumPoolSize(executor.getMaximumPoolSize() + 1);
			} finally {
				lock.unlock();
			}

			executor.submit(r);
		}
	}

	private void waitFor(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
		}
	}
}