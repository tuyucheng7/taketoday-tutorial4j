package cn.tuyucheng.taketoday.forkjoin;

import cn.tuyucheng.taketoday.forkjoin.util.PoolUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;

class Java8ForkJoinIntegrationTest {
	private int[] array;
	private CustomRecursiveTask customRecursiveTask;

	@BeforeEach
	void init() {
		Random random = new Random();
		array = new int[50];
		for (int i = 0; i < array.length; i++) {
			array[i] = random.nextInt(35);
		}
		customRecursiveTask = new CustomRecursiveTask(array);
	}

	@Test
	void callPoolUtil_whenExistsAndExpectedType_thenCorrect() {
		ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
		ForkJoinPool forkJoinPoolTwo = PoolUtil.forkJoinPool;

		assertNotNull(forkJoinPool);
		assertEquals(2, forkJoinPool.getParallelism());
		assertEquals(forkJoinPool, forkJoinPoolTwo);
	}

	@Test
	void callCommonPool_whenExistsAndExpectedType_thenCorrect() {
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		ForkJoinPool commonPoolTwo = ForkJoinPool.commonPool();

		assertNotNull(commonPool);
		assertEquals(commonPool, commonPoolTwo);
	}

	@Test
	void executeRecursiveAction_whenExecuted_thenCorrect() {

		CustomRecursiveAction myRecursiveAction = new CustomRecursiveAction("ddddffffgggghhhh");
		ForkJoinPool.commonPool().invoke(myRecursiveAction);

		assertTrue(myRecursiveAction.isDone());

	}

	@Test
	void executeRecursiveTask_whenExecuted_thenCorrect() {
		ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

		forkJoinPool.execute(customRecursiveTask);
		customRecursiveTask.join();
		assertTrue(customRecursiveTask.isDone());

		forkJoinPool.submit(customRecursiveTask);
		customRecursiveTask.join();
		assertTrue(customRecursiveTask.isDone());
	}

	@Test
	void executeRecursiveTaskWithFJ_whenExecuted_thenCorrect() {
		CustomRecursiveTask customRecursiveTaskFirst = new CustomRecursiveTask(array);
		CustomRecursiveTask customRecursiveTaskSecond = new CustomRecursiveTask(array);
		CustomRecursiveTask customRecursiveTaskLast = new CustomRecursiveTask(array);

		customRecursiveTaskFirst.fork();
		customRecursiveTaskSecond.fork();
		customRecursiveTaskLast.fork();
		int result = 0;
		result += customRecursiveTaskLast.join();
		result += customRecursiveTaskSecond.join();
		result += customRecursiveTaskFirst.join();

		assertTrue(customRecursiveTaskFirst.isDone());
		assertTrue(customRecursiveTaskSecond.isDone());
		assertTrue(customRecursiveTaskLast.isDone());
		assertTrue(result != 0);
	}
}