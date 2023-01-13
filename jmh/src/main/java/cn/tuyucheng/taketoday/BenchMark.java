package cn.tuyucheng.taketoday;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class BenchMark {

	@State(Scope.Benchmark)
	public static class Log {
		public int x = 8;
	}

	@State(Scope.Benchmark)
	public static class ExecutionPlan {

		@Param({"100", "200", "300", "500", "1000"})
		public int iterations;

		public Hasher murmur3;

		public String password = "4v3rys3kur3p455w0rd";

		@Setup(Level.Invocation)
		public void setUp() {
			murmur3 = Hashing.murmur3_128().newHasher();
		}
	}

	@Fork(value = 1, warmups = 1)
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Warmup(iterations = 5)
	public void benchMurmur3_128(ExecutionPlan plan) {

		for (int i = plan.iterations; i > 0; i--) {
			plan.murmur3.putString(plan.password, Charset.defaultCharset());
		}

		plan.murmur3.hash();
	}

	@Benchmark
	@Fork(value = 1, warmups = 1)
	@BenchmarkMode(Mode.Throughput)
	public void init() {
		// Do nothing
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@BenchmarkMode(Mode.AverageTime)
	public void doNothing() {

	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@BenchmarkMode(Mode.AverageTime)
	public void objectCreation() {
		new Object();
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@BenchmarkMode(Mode.AverageTime)
	public Object pillarsOfCreation() {
		return new Object();
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@BenchmarkMode(Mode.AverageTime)
	public void blackHole(Blackhole blackhole) {
		blackhole.consume(new Object());
	}

	@Benchmark
	public double foldedLog() {
		int x = 8;

		return Math.log(x);
	}

	@Benchmark
	public double log(Log input) {
		return Math.log(input.x);
	}

}
