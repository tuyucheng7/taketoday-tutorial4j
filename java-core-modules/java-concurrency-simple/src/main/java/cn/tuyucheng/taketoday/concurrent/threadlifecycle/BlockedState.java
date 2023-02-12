package cn.tuyucheng.taketoday.concurrent.threadlifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockedState {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new DemoThreadB());
		Thread t2 = new Thread(new DemoThreadB());

		t1.start();
		t2.start();

		Thread.sleep(1000);

		LOGGER.info("state of thread t2: {}", t2.getState());
		System.exit(0);
	}

	static class DemoThreadB implements Runnable {
		@Override
		public void run() {
			commonResource();
		}

		public static synchronized void commonResource() {
			while (true) {
				// Infinite loop to mimic heavy processing
				// Thread 't1' won't leave this method
				// when Thread 't2' enters this
			}
		}
	}
}