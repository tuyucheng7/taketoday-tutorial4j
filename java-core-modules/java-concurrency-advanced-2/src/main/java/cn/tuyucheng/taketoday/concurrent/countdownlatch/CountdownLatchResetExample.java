package cn.tuyucheng.taketoday.concurrent.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CountdownLatchResetExample {
	private final int count;
	private final int threadCount;
	private final AtomicInteger updateCount;

	CountdownLatchResetExample(int count, int threadCount) {
		updateCount = new AtomicInteger(0);
		this.count = count;
		this.threadCount = threadCount;
	}

	public int countWaits() {
		CountDownLatch countDownLatch = new CountDownLatch(count);
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			executor.execute(() -> {
				long prevValue = countDownLatch.getCount();
				countDownLatch.countDown();
				if (countDownLatch.getCount() != prevValue) {
					updateCount.incrementAndGet();
				}
			});
		}

		executor.shutdown();
		return updateCount.get();
	}

	public static void main(String[] args) {
		CountdownLatchResetExample ex = new CountdownLatchResetExample(7, 20);
		System.out.println("Count : " + ex.countWaits());
	}
}