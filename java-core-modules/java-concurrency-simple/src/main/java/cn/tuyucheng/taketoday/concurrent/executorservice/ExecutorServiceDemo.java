package cn.tuyucheng.taketoday.concurrent.executorservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceDemo {
	public static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceDemo.class);
	ExecutorService executor = Executors.newFixedThreadPool(10);

	public void execute() {
		executor.submit(() -> {
			new Task();
		});

		executor.shutdown();
		executor.shutdownNow();
		try {
			executor.awaitTermination(20L, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			LOGGER.info("Interrupted!", e);
			Thread.currentThread().interrupt();
		}
	}
}