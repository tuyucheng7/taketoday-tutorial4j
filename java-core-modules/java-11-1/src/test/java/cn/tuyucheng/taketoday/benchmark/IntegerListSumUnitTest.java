package cn.tuyucheng.taketoday.benchmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IntegerListSumUnitTest {

   private IntegerListSum integerListSum;

   @BeforeEach
   public void init() {
      integerListSum = new IntegerListSum();
      integerListSum.setup();
   }

   @Test
   public void whenBenchmarkIsExecute_thenJDKListsMustHaveSameValue() {
      assertEquals(integerListSum.jdkList(), integerListSum.jdkListParallel());
   }

   @Test
   public void whenBenchmarkIsExecute_thenMutableListsMustHaveSameValue() {
      assertEquals(integerListSum.ecMutableList(), integerListSum.ecMutableListParallel());
   }

   @Test
   public void whenBenchmarkIsExecute_thenPrimitiveListsMustHaveSameValue() {
      assertEquals(integerListSum.ecPrimitive(), integerListSum.ecPrimitiveParallel());
   }
}
