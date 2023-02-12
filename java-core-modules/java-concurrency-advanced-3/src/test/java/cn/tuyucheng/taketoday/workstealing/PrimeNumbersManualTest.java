package cn.tuyucheng.taketoday.workstealing;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * This test expects the file target/test-classes/META-INF/BenchmarkList to be present.
 * <p>
 * Before running the test ensure that the file is present.
 * If not, please run mvn install on the module.
 */
class PrimeNumbersManualTest {
	private static final Logger logger = Logger.getAnonymousLogger();

	@Test
	void givenPrimesCalculated_whenUsingPoolsAndOneThread_thenOneThreadSlowest() {
		Options opt = new OptionsBuilder()
			.include(Benchmarked.class.getSimpleName())
			.forks(1)
			.build();

		try {
			new Runner(opt).run();
		} catch (RunnerException e) {
			fail();
		}
	}

	@Test
	void givenNewWorkStealingPool_whenGettingPrimes_thenStealCountChanges() {
		StringBuilder info = new StringBuilder();

		for (int granularity : PrimeNumbers.GRANULARITIES) {
			int parallelism = ForkJoinPool.getCommonPoolParallelism();
			ForkJoinPool pool = (ForkJoinPool) Executors.newWorkStealingPool(parallelism);

			stealCountInfo(info, granularity, pool);
		}
		logger.info("\nExecutors.newWorkStealingPool ->" + info);
	}

	@Test
	void givenCommonPool_whenGettingPrimes_thenStealCountChangesSlowly() {
		StringBuilder info = new StringBuilder();

		for (int granularity : PrimeNumbers.GRANULARITIES) {
			ForkJoinPool pool = ForkJoinPool.commonPool();
			stealCountInfo(info, granularity, pool);
		}
		logger.info("\nForkJoinPool.commonPool ->" + info);
	}

	private void stealCountInfo(StringBuilder info, int granularity, ForkJoinPool forkJoinPool) {
		PrimeNumbers primes = new PrimeNumbers(1, 10000, granularity, new AtomicInteger(0));
		forkJoinPool.invoke(primes);
		forkJoinPool.shutdown();

		long steals = forkJoinPool.getStealCount();
		String output = "\nGranularity: [" + granularity + "], Steals: [" + steals + "]";
		info.append(output);
	}


	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@State(Scope.Benchmark)
	@Fork(value = 2, warmups = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
	public static class Benchmarked {

		@Benchmark
		public void singleThread() {
			PrimeNumbers primes = new PrimeNumbers(10000);
			primes.findPrimeNumbers(); // get prime numbers using a single thread
		}

		@Benchmark
		public void commonPoolBenchmark() {
			PrimeNumbers primes = new PrimeNumbers(10000);
			ForkJoinPool pool = ForkJoinPool.commonPool();
			pool.invoke(primes);
			pool.shutdown();
		}

		@Benchmark
		public void newWorkStealingPoolBenchmark() {
			PrimeNumbers primes = new PrimeNumbers(10000);
			int parallelism = ForkJoinPool.getCommonPoolParallelism();
			ForkJoinPool stealer = (ForkJoinPool) Executors.newWorkStealingPool(parallelism);
			stealer.invoke(primes);
			stealer.shutdown();
		}
	}
}