package cn.tuyucheng.taketoday.arrays;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;

public class ParallelPrefixBenchmark {
    private static final int ARRAY_SIZE = 200_000_000;

    @State(Scope.Benchmark)
    public static class BigArray {

        int[] data;

        @Setup(Level.Iteration)
        public void prepare() {
            data = new int[ARRAY_SIZE];
            for (int j = 0; j < ARRAY_SIZE; j++) {
                data[j] = 1;
            }
        }

        @TearDown(Level.Iteration)
        public void destroy() {
            data = null;
        }

    }

    @Benchmark
    public void largeArrayLoopSum(BigArray bigArray, Blackhole blackhole) {
        for (int i = 0; i < ARRAY_SIZE - 1; i++) {
            bigArray.data[i + 1] += bigArray.data[i];
        }
        blackhole.consume(bigArray.data);
    }

    @Benchmark
    public void largeArrayParallelPrefixSum(BigArray bigArray, Blackhole blackhole) {
        Arrays.parallelPrefix(bigArray.data, (left, right) -> left + right);
        blackhole.consume(bigArray.data);
    }
}