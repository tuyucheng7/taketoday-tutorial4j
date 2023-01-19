package cn.tuyucheng.taketoday.concurrent.stopexecution;

import org.junit.jupiter.api.Test;

import java.util.Timer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeOutTaskUnitTest {

	@Test
	void run() {
		Thread thread = new Thread(new LongRunningTask());
		Timer timer = new Timer();
		TimeOutTask timeOutTask = new TimeOutTask(thread, timer);
		thread.start();
		timeOutTask.run();
		assertTrue(thread.isInterrupted());
	}
}