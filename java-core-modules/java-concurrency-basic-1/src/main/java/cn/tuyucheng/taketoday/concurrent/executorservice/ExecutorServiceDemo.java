package cn.tuyucheng.taketoday.concurrent.executorservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceDemo {
	ExecutorService executor = Executors.newFixedThreadPool(10);

	public void execute() {
		executor.submit(Task::new);
		executor.shutdown();
		executor.shutdownNow();
	}
}