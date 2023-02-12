package cn.tuyucheng.taketoday.concurrent.threadlifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TerminatedState implements Runnable {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new TerminatedState());
		t1.start();
		Thread.sleep(1000);
		LOGGER.info("state of thread t1: {}", t1.getState());
	}

	@Override
	public void run() {
		// No processing in this block
	}
}