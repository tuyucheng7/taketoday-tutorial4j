package cn.tuyucheng.taketoday.concurrent.threadsafety;

import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.ReentranReadWriteLockCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.ReentrantReadWriteLockCounter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ReentrantReadWriteLockCounterUnitTest {

	@Test
	void whenCalledIncrementCounter_thenCorrect() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		ReentrantReadWriteLockCounter counter = new ReentrantReadWriteLockCounter();
		Future<Integer> future1 = executorService.submit(new ReentranReadWriteLockCounterCallable(counter));
		Future<Integer> future2 = executorService.submit(new ReentranReadWriteLockCounterCallable(counter));

		// Just to make sure both are completed
		future1.get();
		future2.get();

		assertThat(counter.getCounter()).isEqualTo(2);
	}
}