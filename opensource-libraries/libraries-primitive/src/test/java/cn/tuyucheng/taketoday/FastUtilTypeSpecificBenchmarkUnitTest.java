package cn.tuyucheng.taketoday;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class FastUtilTypeSpecificBenchmarkUnitTest {

	@Param({"100", "1000", "10000", "100000"})
	public int setSize;

	@Benchmark
	public IntSet givenFastUtilsIntSetWithInitialSizeSet_whenPopulated_checkTimeTaken() {
		IntSet intSet = new IntOpenHashSet(setSize);
		for (int i = 0; i < setSize; i++) {
			intSet.add(i);
		}
		return intSet;
	}


	@Benchmark
	public Set<Integer> givenCollectionsHashSetWithInitialSizeSet_whenPopulated_checkTimeTaken() {
		Set<Integer> intSet = new HashSet<Integer>(setSize);
		for (int i = 0; i < setSize; i++) {
			intSet.add(i);
		}
		return intSet;
	}

	public static void main(String... args) throws RunnerException {
		Options opts = new OptionsBuilder()
			.include(".*")
			.warmupIterations(1)
			.measurementIterations(2)
			.jvmArgs("-Xms2g", "-Xmx2g")
			.shouldDoGC(true)
			.forks(1)
			.build();

		new Runner(opts).run();
	}


}
