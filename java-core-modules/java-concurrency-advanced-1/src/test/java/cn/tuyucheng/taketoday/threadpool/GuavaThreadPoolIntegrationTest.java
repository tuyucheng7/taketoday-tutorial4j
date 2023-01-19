package cn.tuyucheng.taketoday.threadpool;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuavaThreadPoolIntegrationTest {

	@Test
	void whenExecutingTaskWithDirectExecutor_thenTheTaskIsExecutedInTheCurrentThread() {
		Executor executor = MoreExecutors.directExecutor();
		AtomicBoolean executed = new AtomicBoolean();

		executor.execute(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executed.set(true);
		});

		assertTrue(executed.get());
	}

	@Test
	void whenJoiningFuturesWithAllAsList_thenCombinedFutureCompletesAfterAllFuturesComplete() throws ExecutionException, InterruptedException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

		ListenableFuture<String> future1 = listeningExecutorService.submit(() -> "Hello");
		ListenableFuture<String> future2 = listeningExecutorService.submit(() -> "World");

		String greeting = String.join(" ", Futures.allAsList(future1, future2).get());
		assertEquals("Hello World", greeting);
	}
}