package cn.tuyucheng.taketoday.concurrent.stopexecution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedTimeTaskUnitTest {

	@Test
	void run() throws InterruptedException {
		long start = System.currentTimeMillis();
		Thread thread = new Thread(new FixedTimeTask(10));
		thread.start();
		thread.join();
		long end = System.currentTimeMillis();
		assertTrue(end - start >= 10);
	}
}