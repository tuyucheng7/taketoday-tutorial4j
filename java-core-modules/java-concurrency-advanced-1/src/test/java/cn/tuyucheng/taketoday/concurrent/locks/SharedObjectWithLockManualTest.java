package cn.tuyucheng.taketoday.concurrent.locks;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SharedObjectWithLockManualTest {

	@Test
	void whenLockAcquired_ThenLockedIsTrue() {
		final SharedObjectWithLock object = new SharedObjectWithLock();

		final int threadCount = 2;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);

		executeThreads(object, threadCount, service);

		assertTrue(object.isLocked());

		service.shutdown();
	}

	@Test
	void whenLocked_ThenQueuedThread() {
		final int threadCount = 4;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);
		final SharedObjectWithLock object = new SharedObjectWithLock();

		executeThreads(object, threadCount, service);

		assertTrue(object.hasQueuedThreads());

		service.shutdown();
	}

	void whenTryLock_ThenQueuedThread() {
		final SharedObjectWithLock object = new SharedObjectWithLock();

		final int threadCount = 2;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);

		executeThreads(object, threadCount, service);

		assertTrue(object.isLocked());

		service.shutdown();
	}

	@Test
	void whenGetCount_ThenCorrectCount() throws InterruptedException {
		final int threadCount = 4;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);
		final SharedObjectWithLock object = new SharedObjectWithLock();

		executeThreads(object, threadCount, service);
		Thread.sleep(1000);
		assertEquals(object.getCounter(), 4);

		service.shutdown();
	}

	private void executeThreads(SharedObjectWithLock object, int threadCount, ExecutorService service) {
		for (int i = 0; i < threadCount; i++) {
			service.execute(object::perform);
		}
	}
}