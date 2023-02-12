package cn.tuyucheng.taketoday.concurrent.waitandnotify;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class Sender implements Runnable {
	private final Data data;

	public Sender(Data data) {
		this.data = data;
	}

	@Override
	public void run() {
		String[] packets = {
				"First packet",
				"Second packet",
				"Third packet",
				"Fourth packet",
				"End"
		};

		for (String packet : packets) {
			data.send(packet);

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