package cn.tuyucheng.taketoday.exchanger;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Exchanger;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangerUnitTest {

	@Test
	void givenThreads_whenMessageExchanged_thenCorrect() {
		Exchanger<String> exchanger = new Exchanger<>();

		Runnable taskA = () -> {
			try {
				String message = exchanger.exchange("from A");
				assertEquals("from B", message);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		};

		Runnable taskB = () -> {
			try {
				String message = exchanger.exchange("from B");
				assertEquals("from A", message);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		};

		CompletableFuture.allOf(runAsync(taskA), runAsync(taskB)).join();
	}

	@Test
	void givenThread_whenExchangedMessage_thenCorrect() throws InterruptedException {
		Exchanger<String> exchanger = new Exchanger<>();

		Runnable runner = () -> {
			try {
				String message = exchanger.exchange("from runner");
				assertEquals("to runner", message);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		};

		CompletableFuture<Void> result = runAsync(runner);
		String msg = exchanger.exchange("to runner");
		assertEquals("from runner", msg);
		result.join();
	}
}