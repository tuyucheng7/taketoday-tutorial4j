package cn.tuyucheng.taketoday.concurrent.daemon;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DaemonThreadUnitTest {

	@Test
	@Disabled
	void whenCallIsDaemon_thenCorrect() {
		NewThread daemonThread = new NewThread();
		NewThread userThread = new NewThread();
		daemonThread.setDaemon(true);
		daemonThread.start();
		userThread.start();

		assertTrue(daemonThread.isDaemon());
		assertFalse(userThread.isDaemon());
	}

	@Test
	@Disabled
	void givenUserThread_whenSetDaemonWhileRunning_thenIllegalThreadStateException() {
		NewThread daemonThread = new NewThread();
		daemonThread.start();
		assertThrows(IllegalThreadStateException.class, () -> daemonThread.setDaemon(true));
	}
}