package cn.tuyucheng.taketoday.concurrent.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SimpleRunnable implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(SimpleRunnable.class);

	private final String message;

	SimpleRunnable(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		log.info(message);
	}
}