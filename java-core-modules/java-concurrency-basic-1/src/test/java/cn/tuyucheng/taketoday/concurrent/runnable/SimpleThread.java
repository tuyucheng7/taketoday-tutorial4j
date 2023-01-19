package cn.tuyucheng.taketoday.concurrent.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SimpleThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(SimpleThread.class);

	private final String message;

	SimpleThread(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		log.info(message);
	}
}