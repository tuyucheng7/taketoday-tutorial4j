package cn.tuyucheng.taketoday.concurrent.threadsafety;

import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.ReentrantLockCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.ReentrantLockCounter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ReentrantLockCounterUnitTest {

	@Test
	void whenCalledIncrementCounter_thenCorrect() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		ReentrantLockCounter counter = new ReentrantLockCounter();
		Future<Integer> future1 = executorService.submit(new ReentrantLockCounterCallable(counter));
		Future<Integer> future2 = executorService.submit(new ReentrantLockCounterCallable(counter));

		// Just to make sure both are completed
		future1.get();
		future2.get();

		assertThat(counter.getCounter()).isEqualTo(2);
	}
}