package cn.tuyucheng.taketoday.concurrent.volatilekeyword;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SharedObjectManualTest {

	@Test
	void whenOneThreadWrites_thenVolatileReadsFromMainMemory() throws InterruptedException {
		SharedObject sharedObject = new SharedObject();

		Thread writer = new Thread(sharedObject::incrementCount);
		writer.start();
		Thread.sleep(100);

		Thread readerOne = new Thread(() -> {
			int valueReadByThread2 = sharedObject.getCount();
			assertEquals(1, valueReadByThread2);
		});
		readerOne.start();

		Thread readerTwo = new Thread(() -> {
			int valueReadByThread3 = sharedObject.getCount();
			assertEquals(1, valueReadByThread3);
		});
		readerTwo.start();

	}

	@Test
	void whenTwoThreadWrites_thenVolatileReadsFromMainMemory() throws InterruptedException {
		SharedObject sharedObject = new SharedObject();
		Thread writerOne = new Thread(sharedObject::incrementCount);
		writerOne.start();
		Thread.sleep(100);

		Thread writerTwo = new Thread(sharedObject::incrementCount);
		writerTwo.start();
		Thread.sleep(100);

		Thread readerOne = new Thread(() -> {
			int valueReadByThread2 = sharedObject.getCount();
			assertEquals(2, valueReadByThread2);
		});
		readerOne.start();

		Thread readerTwo = new Thread(() -> {
			int valueReadByThread3 = sharedObject.getCount();
			assertEquals(2, valueReadByThread3);
		});
		readerTwo.start();
	}
}