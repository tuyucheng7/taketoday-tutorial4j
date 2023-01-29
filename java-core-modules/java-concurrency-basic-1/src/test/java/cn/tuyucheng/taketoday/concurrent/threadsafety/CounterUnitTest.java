package cn.tuyucheng.taketoday.concurrent.threadsafety;

import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.CounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.Counter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class CounterUnitTest {

	@Test
	void whenCalledIncrementCounter_thenCorrect() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		Counter counter = new Counter();
		Future<Integer> future1 = executorService.submit(new CounterCallable(counter));
		Future<Integer> future2 = executorService.submit(new CounterCallable(counter));

		// Just to make sure both are completed
		future1.get();
		future2.get();

		assertThat(counter.getCounter()).isEqualTo(2);
	}
}