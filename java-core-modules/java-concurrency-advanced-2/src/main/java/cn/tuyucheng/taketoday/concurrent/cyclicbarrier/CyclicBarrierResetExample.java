package cn.tuyucheng.taketoday.concurrent.cyclicbarrier;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrierResetExample {
	private final int count;
	private final int threadCount;
	private final AtomicInteger updateCount;

	CyclicBarrierResetExample(int count, int threadCount) {
		updateCount = new AtomicInteger(0);
		this.count = count;
		this.threadCount = threadCount;
	}

	public int countWaits() {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(count);

		ExecutorService es = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			es.execute(() -> {
				try {
					if (cyclicBarrier.getNumberWaiting() > 0) {
						updateCount.incrementAndGet();
					}
					cyclicBarrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					Thread.currentThread().interrupt();
				}
			});
		}
		es.shutdown();
		try {
			es.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return updateCount.get();
	}

	public static void main(String[] args) {
		CyclicBarrierResetExample ex = new CyclicBarrierResetExample(7, 20);
		System.out.println("Count : " + ex.countWaits());
	}
}