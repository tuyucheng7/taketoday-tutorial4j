package cn.tuyucheng.taketoday.concurrent.daemon;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewThread extends Thread {

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		while (true) {
			for (int i = 0; i < 10; i++) {
				LOGGER.info("{}: New Thread is running... {}", this.getName(), i);
				try {
					// Wait for one sec, so it doesn't print too fast
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					LOGGER.error("context", e);
				}
			}
			// prevent the Thread to run forever. It will finish its execution after 2 seconds
			if (System.currentTimeMillis() - startTime > 2000) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}