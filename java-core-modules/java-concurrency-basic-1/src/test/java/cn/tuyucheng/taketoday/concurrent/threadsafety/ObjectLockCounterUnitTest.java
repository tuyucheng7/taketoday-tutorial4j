package cn.tuyucheng.taketoday.concurrent.threadsafety;

import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.ExtrinsicLockCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.ObjectLockCounter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectLockCounterUnitTest {

	@Test
	void whenCalledIncrementCounter_thenCorrect() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		ObjectLockCounter counter = new ObjectLockCounter();
		Future<Integer> future1 = executorService.submit(new ExtrinsicLockCounterCallable(counter));
		Future<Integer> future2 = executorService.submit(new ExtrinsicLockCounterCallable(counter));

		// Just to make sure both are completed
		future1.get();
		future2.get();

		assertThat(counter.getCounter()).isEqualTo(2);
	}
}