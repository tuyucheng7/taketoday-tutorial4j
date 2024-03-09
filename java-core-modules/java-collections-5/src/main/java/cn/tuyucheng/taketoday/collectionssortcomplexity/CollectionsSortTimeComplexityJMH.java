package cn.tuyucheng.taketoday.collectionssortcomplexity;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class CollectionsSortTimeComplexityJMH {

   public static void main(String[] args) throws Exception {
      org.openjdk.jmh.Main.main(args);

   }

   @Benchmark
   public void measureCollectionsSortBestCase(BestCaseBenchmarkState state) {
      List<Integer> sortedList = new ArrayList<>(state.sortedList);
      Collections.sort(sortedList);
   }

   @Benchmark
   public void measureCollectionsSortAverageWorstCase(AverageWorstCaseBenchmarkState state) {
      List<Integer> unsortedList = new ArrayList<>(state.unsortedList);
      Collections.sort(unsortedList);
   }

   @State(Scope.Benchmark)
   public static class BestCaseBenchmarkState {
      List<Integer> sortedList;

      @Setup(Level.Trial)
      public void setUp() {
         sortedList = new ArrayList<>();
         for (int i = 1; i <= 1000000; i++) {
            sortedList.add(i);
         }
      }
   }

   @State(Scope.Benchmark)
   public static class AverageWorstCaseBenchmarkState {
      List<Integer> unsortedList;

      @Setup(Level.Trial)
      public void setUp() {
         unsortedList = new ArrayList<>();
         for (int i = 1000000; i > 0; i--) {
            unsortedList.add(i);
         }
      }
   }

}