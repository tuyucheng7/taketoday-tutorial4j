package cn.tuyucheng.taketoday.concurrent.waitandnotify;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Data {
	private String packet;

	// True if receiver should wait, False if sender should wait
	private boolean transfer = true;

	public synchronized String receive() {
		while (transfer) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.info("Thread interrupted");
			}
		}
		transfer = true;

		String returnPacket = packet;
		notifyAll();
		return returnPacket;
	}

	public synchronized void send(String packet) {
		while (!transfer) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.info("Thread interrupted");
			}

			transfer = false;
			this.packet = packet;
			notifyAll();
		}
	}
}