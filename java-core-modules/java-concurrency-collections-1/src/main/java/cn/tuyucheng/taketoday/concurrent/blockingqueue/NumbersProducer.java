package cn.tuyucheng.taketoday.concurrent.blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class NumbersProducer implements Runnable {
	private final BlockingQueue<Integer> numberQueue;
	private final int poisonPill;
	private final int poisonPillPerProducer;

	public NumbersProducer(BlockingQueue<Integer> numberQueue, int poisonPill, int poisonPillPerProducer) {
		this.numberQueue = numberQueue;
		this.poisonPill = poisonPill;
		this.poisonPillPerProducer = poisonPillPerProducer;
	}

	@Override
	public void run() {
		try {
			generateNumbers();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void generateNumbers() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			numberQueue.put(ThreadLocalRandom.current().nextInt(100));
		}
		for (int i = 0; i < poisonPillPerProducer; i++) {
			numberQueue.put(poisonPill);
		}
	}
}