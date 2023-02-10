package cn.tuyucheng.taketoday.binarysemaphorereentrantlock;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinarySemaphoreVsReentrantLockUnitTest {

	@Test
	void givenBinarySemaphore_whenAcquireAndRelease_thenCheckAvailablePermits() throws InterruptedException {
		Semaphore binarySemaphore = new Semaphore(1);
		try {
			binarySemaphore.acquire();
			assertEquals(0, binarySemaphore.availablePermits());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			binarySemaphore.release();
			assertEquals(1, binarySemaphore.availablePermits());
		}
	}

	@Test
	void givenReentrantLock_whenLockAndUnlock_thenCheckHoldCountAndIsLocked() throws InterruptedException {
		ReentrantLock reentrantLock = new ReentrantLock();
		try {
			reentrantLock.lock();
			assertEquals(1, reentrantLock.getHoldCount());
			assertTrue(reentrantLock.isLocked());
		} finally {
			reentrantLock.unlock();
			assertEquals(0, reentrantLock.getHoldCount());
			assertFalse(reentrantLock.isLocked());
		}
	}

	@Test
	void givenReentrantLock_whenLockMultipleTimes_thenUnlockMultipleTimesToRelease() throws InterruptedException {
		ReentrantLock reentrantLock = new ReentrantLock();
		try {
			reentrantLock.lock();
			reentrantLock.lock();
			assertEquals(2, reentrantLock.getHoldCount());
			assertTrue(reentrantLock.isLocked());
		} finally {
			reentrantLock.unlock();
			assertEquals(1, reentrantLock.getHoldCount());
			assertTrue(reentrantLock.isLocked());

			reentrantLock.unlock();
			assertEquals(0, reentrantLock.getHoldCount());
			assertFalse(reentrantLock.isLocked());
		}
	}
}