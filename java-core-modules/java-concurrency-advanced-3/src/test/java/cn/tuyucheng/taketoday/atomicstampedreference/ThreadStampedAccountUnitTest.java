package cn.tuyucheng.taketoday.atomicstampedreference;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadStampedAccountUnitTest {

	@Test
	void givenMultiThread_whenStampedAccount_thenSetBalance() throws InterruptedException {
		StampedAccount account = new StampedAccount();

		Thread t1 = new Thread(() -> {
			while (!account.deposit(100)) {
				Thread.yield();
			}
		});
		t1.start();

		Thread t2 = new Thread(() -> {
			while (!account.withdrawal(100)) {
				Thread.yield();
			}
		});
		t2.start();

		t1.join(10_000);
		t2.join(10_000);

		assertFalse(t1.isAlive());
		assertFalse(t2.isAlive());

		assertEquals(0, account.getBalance());
		assertTrue(account.getStamp() > 0);
	}
}