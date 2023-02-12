package cn.tuyucheng.taketoday.concurrent.threadlifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimedWaitingState {

	public static void main(String[] args) throws InterruptedException {
		DemoThread obj1 = new DemoThread();
		Thread t1 = new Thread(obj1);
		t1.start();
		// The following sleep will give enough time for ThreadScheduler
		// to start processing of thread t1
		Thread.sleep(1000);
		LOGGER.info("state of thread t1: {}", t1.getState());
	}

	static class DemoThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error("context", e);
			}
		}
	}
}