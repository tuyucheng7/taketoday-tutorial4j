package cn.tuyucheng.taketoday.primitive;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Thread)
public class LongWrapperLookup extends Lookup {
	private Long[] elements;
	private final long pivot = 2;

	@Override
	@Setup
	public void prepare() {
		long common = 1;
		elements = new Long[s];
		for (int i = 0; i < s - 1; i++) {
			elements[i] = common;
		}
		elements[s - 1] = pivot;
	}

	@Override
	@TearDown
	public void clean() {
		elements = null;
	}

	@Override
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public int findPosition() {
		int index = 0;
		Long pivotWrapper = pivot;
		while (!pivotWrapper.equals(elements[index])) {
			index++;
		}
		return index;

	}

	@Override
	public String getSimpleClassName() {
		return LongWrapperLookup.class.getSimpleName();
	}

}
