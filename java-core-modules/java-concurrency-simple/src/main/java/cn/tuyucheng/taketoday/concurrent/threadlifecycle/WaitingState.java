package cn.tuyucheng.taketoday.concurrent.threadlifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitingState implements Runnable {
	private static Thread t1;

	public static void main(String[] args) {
		t1 = new Thread(new WaitingState());
		t1.start();
	}

	public void run() {
		Thread t2 = new Thread(new DemoThreadWS());
		t2.start();

		try {
			t2.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("context", e);
		}
	}

	static class DemoThreadWS implements Runnable {
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error("context", e);
			}

			LOGGER.info("state of thread t1: {}", WaitingState.t1.getState());
		}
	}
}