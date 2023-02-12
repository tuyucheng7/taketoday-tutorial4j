package cn.tuyucheng.taketoday.concurrent.waitandnotify;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class Receiver implements Runnable {
	private final Data data;

	public Receiver(Data data) {
		this.data = data;
	}

	@Override
	public void run() {
		for (String receivedMessage = data.receive(); !"End".equals(receivedMessage); receivedMessage = data.receive()) {
			LOGGER.info(receivedMessage);

			// Thread.sleep() to mimic heavy server-side processing
			try {
				SecureRandom random = new SecureRandom();
				Thread.sleep(random.nextInt(1000, 5000));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error("context ", e);
			}
		}
	}
}