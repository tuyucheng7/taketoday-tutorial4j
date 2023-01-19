package cn.tuyucheng.taketoday.threadlocalrandom;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ThreadLocalRandomBenchMarker {
	private final Random random = new Random();

	@Benchmark
	public int randomValuesUsingRandom() {
		return random.nextInt();
	}

	@Benchmark
	public int randomValuesUsingThreadLocalRandom() {
		return ThreadLocalRandom.current().nextInt();
	}
}