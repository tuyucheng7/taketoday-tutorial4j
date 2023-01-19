package cn.tuyucheng.taketoday.concurrent.lock;

import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Fork(value = 2)
@Warmup(iterations = 0)
public class ConcurrentAccessBenchmark {
	static final int SLOTS = 4;
	static final int THREADS = 10000;
	static final int BUCKETS = Runtime.getRuntime().availableProcessors() * SLOTS;
	SingleLock singleLock = new SingleLock();
	StripedLock stripedLock = new StripedLock(BUCKETS);

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public Map<String, String> singleLockHashMap() throws InterruptedException {
		return singleLock.doWork(new HashMap<>(), THREADS, SLOTS);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public Map<String, String> stripedLockHashMap() throws InterruptedException {
		return stripedLock.doWork(new HashMap<>(), THREADS, SLOTS);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public Map<String, String> singleLockConcurrentHashMap() throws InterruptedException {
		return singleLock.doWork(new ConcurrentHashMap<>(), THREADS, SLOTS);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public Map<String, String> stripedLockConcurrentHashMap() throws InterruptedException {
		return stripedLock.doWork(new ConcurrentHashMap<>(), THREADS, SLOTS);
	}
}