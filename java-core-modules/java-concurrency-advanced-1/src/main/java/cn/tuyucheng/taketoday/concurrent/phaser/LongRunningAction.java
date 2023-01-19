package cn.tuyucheng.taketoday.concurrent.phaser;

import java.util.concurrent.Phaser;

public class LongRunningAction implements Runnable {
	private final String threadName;
	private final Phaser phaser;

	public LongRunningAction(String threadName, Phaser phaser) {
		this.threadName = threadName;
		this.phaser = phaser;
	}

	@Override
	public void run() {
		System.out.println("This is phase " + phaser.getPhase());
		System.out.println("Thread " + threadName + " before long running action");

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Thread " + threadName + " action completed and waiting for others");
		phaser.arriveAndAwaitAdvance();
		System.out.println("Thread " + threadName + " proceeding in phase " + phaser.getPhase());

		phaser.arriveAndDeregister();
	}
}