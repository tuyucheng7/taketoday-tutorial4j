package cn.tuyucheng.taketoday.concurrent.runnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskRunner {

	public static void main(String[] args) {
		executeTask();
	}

	private static void executeTask() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		EventLoggingTask task = new EventLoggingTask();
		Future<?> future = executorService.submit(task);
		executorService.shutdown();
	}
}