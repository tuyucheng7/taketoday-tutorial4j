package cn.tuyucheng.taketoday.concurrent.locks;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynchronizedHashMapWithRWLockManualTest {

	@Test
	void whenWriting_ThenNoReading() {
		SynchronizedHashMapWithRWLock object = new SynchronizedHashMapWithRWLock();
		final int threadCount = 3;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);

		executeWriterThreads(object, threadCount, service);

		assertFalse(object.isReadLockAvailable());

		service.shutdown();
	}

	@Test
	void whenReading_ThenMultipleReadingAllowed() {
		SynchronizedHashMapWithRWLock object = new SynchronizedHashMapWithRWLock();
		final int threadCount = 5;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);

		executeReaderThreads(object, threadCount, service);

		assertTrue(object.isReadLockAvailable());

		service.shutdown();
	}

	private void executeWriterThreads(SynchronizedHashMapWithRWLock object, int threadCount, ExecutorService service) {
		for (int i = 0; i < threadCount; i++) {
			service.execute(() -> {
				try {
					object.put("key" + threadCount, "value" + threadCount);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void executeReaderThreads(SynchronizedHashMapWithRWLock object, int threadCount, ExecutorService service) {
		for (int i = 0; i < threadCount; i++)
			service.execute(() -> object.get("key" + threadCount));
	}
}