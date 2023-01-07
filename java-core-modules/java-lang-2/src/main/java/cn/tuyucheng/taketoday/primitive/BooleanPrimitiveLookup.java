package cn.tuyucheng.taketoday.primitive;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Thread)
public class BooleanPrimitiveLookup extends Lookup {

	private boolean[] elements;
	private final boolean pivot = false;

	@Setup
	@Override
	public void prepare() {
		elements = new boolean[s];
		for (int i = 0; i < s - 1; i++) {
			elements[i] = true;
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
		return BooleanPrimitiveLookup.class.getSimpleName();
	}


}
