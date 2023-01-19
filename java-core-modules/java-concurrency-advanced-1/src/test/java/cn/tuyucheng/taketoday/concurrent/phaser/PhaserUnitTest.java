package cn.tuyucheng.taketoday.concurrent.phaser;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
class PhaserUnitTest {

	@Test
	void givenPhaser_whenCoordinateWorksBetweenThreads_thenShouldCoordinateBetweenMultiplePhases() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Phaser ph = new Phaser(1);
		assertEquals(0, ph.getPhase());

		executorService.submit(new LongRunningAction("thread-1", ph));
		executorService.submit(new LongRunningAction("thread-2", ph));
		executorService.submit(new LongRunningAction("thread-3", ph));

		System.out.println("Thread " + Thread.currentThread().getName() + " waiting for others");
		ph.arriveAndAwaitAdvance();
		System.out.println("Thread " + Thread.currentThread().getName() + " proceeding in phase " + ph.getPhase());

		assertEquals(1, ph.getPhase());

		executorService.submit(new LongRunningAction("thread-4", ph));
		executorService.submit(new LongRunningAction("thread-5", ph));

		System.out.println("Thread " + Thread.currentThread().getName() + " waiting for others");
		ph.arriveAndAwaitAdvance();
		System.out.println("Thread " + Thread.currentThread().getName() + " proceeding in phase " + ph.getPhase());

		assertEquals(2, ph.getPhase());

		ph.arriveAndDeregister();
	}
}