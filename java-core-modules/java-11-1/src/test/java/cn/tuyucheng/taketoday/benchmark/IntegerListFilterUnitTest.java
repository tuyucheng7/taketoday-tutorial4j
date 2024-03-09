package cn.tuyucheng.taketoday.benchmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IntegerListFilterUnitTest {

   private IntegerListFilter integerListFilter;

   @BeforeEach
   public void init() {
      integerListFilter = new IntegerListFilter();
      integerListFilter.setup();
   }

   @Test
   public void whenBenchmarkIsExecute_thenJDKListsMustBeOfSameSize() {
      assertEquals(integerListFilter.jdkList().size(), integerListFilter.jdkListParallel().size());
   }

   @Test
   public void whenBenchmarkIsExecute_thenMutableListsMustBeOfSameSize() {
      assertEquals(integerListFilter.ecMutableList().size(), integerListFilter.ecMutableListParallel().size());
   }

   @Test
   public void whenBenchmarkIsExecute_thenPrimitiveListsMustBeOfSameSize() {
      assertEquals(integerListFilter.ecPrimitive().size(), integerListFilter.ecPrimitiveParallel().size());
   }
}
