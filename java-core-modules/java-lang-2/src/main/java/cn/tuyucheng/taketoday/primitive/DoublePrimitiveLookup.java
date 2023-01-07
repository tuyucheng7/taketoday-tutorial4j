package cn.tuyucheng.taketoday.primitive;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Thread)
public class DoublePrimitiveLookup extends Lookup {
	private double[] elements;
	private final double pivot = 2;

	@Setup
	@Override
	public void prepare() {
		double common = 1;
		elements = new double[s];
		for (int i = 0; i < s - 1; i++) {
			elements[i] = common;
		}
		elements[s - 1] = pivot;
	}

	@TearDown
	@Override
	public void clean() {
		elements = null;
	}


	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public int findPosition() {
		int index = 0;
		while (pivot != elements[index]) {
			index++;
		}
		return index;
	}

	@Override
	public String getSimpleClassName() {
		return DoublePrimitiveLookup.class.getSimpleName();
	}
}
