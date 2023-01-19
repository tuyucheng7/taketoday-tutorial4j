package cn.tuyucheng.taketoday.concurrent.stopexecution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LongRunningTaskUnitTest {

	@Test
	void run() {
		Thread thread = new Thread(new LongRunningTask());
		thread.start();
		assertTrue(thread.isAlive());

		thread.interrupt();
		assertTrue(thread.isInterrupted());
	}
}