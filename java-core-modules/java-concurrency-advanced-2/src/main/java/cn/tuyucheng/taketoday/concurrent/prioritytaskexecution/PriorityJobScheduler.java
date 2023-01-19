package cn.tuyucheng.taketoday.concurrent.prioritytaskexecution;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PriorityJobScheduler {
	private final ExecutorService priorityJobPoolExecutor;
	private final ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
	private final PriorityBlockingQueue<Job> priorityQueue;

	public PriorityJobScheduler(Integer poolSize, Integer queueSize) {
		priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
		priorityQueue = new PriorityBlockingQueue<>(queueSize, Comparator.comparing(Job::getJobPriority));

		priorityJobScheduler.execute(() -> {
			while (true) {
				try {
					priorityJobPoolExecutor.execute(priorityQueue.take());
				} catch (InterruptedException e) {
					// exception needs special handling
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
	}

	public void scheduleJob(Job job) {
		priorityQueue.add(job);
	}

	public int getQueuedTaskCount() {
		return priorityQueue.size();
	}

	protected void close(ExecutorService scheduler) {
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
		}
	}

	public void closeScheduler() {
		close(priorityJobPoolExecutor);
		close(priorityJobScheduler);
	}
}