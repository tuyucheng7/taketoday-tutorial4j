package cn.tuyucheng.taketoday.concurrent.semaphores;

import org.apache.commons.lang3.concurrent.TimedSemaphore;

import java.util.concurrent.TimeUnit;

public class DelayQueueUsingTimedSemaphore {
	private final TimedSemaphore semaphore;

	DelayQueueUsingTimedSemaphore(long period, int slotLimit) {
		semaphore = new TimedSemaphore(period, TimeUnit.SECONDS, slotLimit);
	}

	boolean tryAdd() {
		return semaphore.tryAcquire();
	}

	int availableSlots() {
		return semaphore.getAvailablePermits();
	}
}