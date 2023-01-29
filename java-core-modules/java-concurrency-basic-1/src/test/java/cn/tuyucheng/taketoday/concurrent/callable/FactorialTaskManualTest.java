package cn.tuyucheng.taketoday.concurrent.callable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactorialTaskManualTest {

	private ExecutorService executorService;

	@BeforeEach
	void setup() {
		executorService = Executors.newSingleThreadExecutor();
	}

	@Test
	void whenTaskSubmitted_ThenFutureResultObtained() throws ExecutionException, InterruptedException {
		FactorialTask task = new FactorialTask(5);
		Future<Integer> future = executorService.submit(task);
		assertEquals(120, future.get().intValue());
	}

	@Test
	void whenException_ThenCallableThrowsIt() throws ExecutionException, InterruptedException {
		FactorialTask task = new FactorialTask(-5);
		Future<Integer> future = executorService.submit(task);
		assertThrows(ExecutionException.class, future::get);
	}

	@Test
	void whenException_ThenCallableDoesntThrowsItIfGetIsNotCalled() {
		FactorialTask task = new FactorialTask(-5);
		Future<Integer> future = executorService.submit(task);
		assertFalse(future.isDone());
	}

	@AfterEach
	void cleanup() {
		executorService.shutdown();
	}
}