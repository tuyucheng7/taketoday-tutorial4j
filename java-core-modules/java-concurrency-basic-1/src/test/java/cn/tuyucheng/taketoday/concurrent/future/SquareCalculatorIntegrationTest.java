package cn.tuyucheng.taketoday.concurrent.future;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class SquareCalculatorIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(SquareCalculatorIntegrationTest.class);

	@Rule
	public TestName name = new TestName();

	private long start;

	private SquareCalculator squareCalculator;

	@Test
	void givenExecutorIsSingleThreaded_whenTwoExecutionsAreTriggered_thenRunInSequence() throws InterruptedException, ExecutionException {
		squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());

		Future<Integer> result1 = squareCalculator.calculate(4);
		Future<Integer> result2 = squareCalculator.calculate(1000);

		while (!result1.isDone() || !result2.isDone()) {
			LOG.debug(String.format("Task 1 is %s and Task 2 is %s.", result1.isDone() ? "done" : "not done", result2.isDone() ? "done" : "not done"));
			Thread.sleep(300);
		}

		assertEquals(16, result1.get().intValue());
		assertEquals(1000000, result2.get().intValue());
	}

	@Test
	void whenGetWithTimeoutLowerThanExecutionTime_thenThrowException() throws InterruptedException, ExecutionException, TimeoutException {
		squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());
		Future<Integer> result = squareCalculator.calculate(4);
		assertThrows(TimeoutException.class, () -> result.get(500, TimeUnit.MILLISECONDS));
	}

	@Test
	void givenExecutorIsMultiThreaded_whenTwoExecutionsAreTriggered_thenRunInParallel() throws InterruptedException, ExecutionException {
		squareCalculator = new SquareCalculator(Executors.newFixedThreadPool(2));

		Future<Integer> result1 = squareCalculator.calculate(4);
		Future<Integer> result2 = squareCalculator.calculate(1000);

		while (!result1.isDone() || !result2.isDone()) {
			LOG.debug(String.format("Task 1 is %s and Task 2 is %s.", result1.isDone() ? "done" : "not done", result2.isDone() ? "done" : "not done"));

			Thread.sleep(300);
		}

		assertEquals(16, result1.get().intValue());
		assertEquals(1000000, result2.get().intValue());
	}

	@Test
	void whenCancelFutureAndCallGet_thenThrowException() throws InterruptedException, ExecutionException, TimeoutException {
		squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());

		Future<Integer> result = squareCalculator.calculate(4);

		boolean canceled = result.cancel(true);

		assertTrue(canceled, "Future was canceled");
		assertTrue(result.isCancelled(), "Future was canceled");

		assertThrows(CancellationException.class, result::get);
	}

	@BeforeEach
	void start() {
		start = System.currentTimeMillis();
	}

	@AfterEach
	void tearDown() {
		LOG.debug(String.format("Test %s took %s ms \n", name.getMethodName(), System.currentTimeMillis() - start));
	}
}