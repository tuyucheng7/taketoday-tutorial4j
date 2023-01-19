package cn.tuyucheng.taketoday.concurrent.accumulator;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LongAccumulatorUnitTest {

	@Test
	void givenLongAccumulator_whenApplyActionOnItFromMultipleThreads_thenShouldProduceProperResult() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		LongBinaryOperator sum = Long::sum;
		LongAccumulator accumulator = new LongAccumulator(sum, 0L);
		int numberOfThreads = 4;
		int numberOfIncrements = 100;

		Runnable accumulateAction = () -> IntStream
				.rangeClosed(0, numberOfIncrements)
				.forEach(accumulator::accumulate);

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.execute(accumulateAction);
		}

		executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
		executorService.shutdown();
		assertEquals(accumulator.get(), 20200);
	}
}