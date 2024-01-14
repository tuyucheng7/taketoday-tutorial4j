package cn.tuyucheng.taketoday.concurrent.cyclicbarrier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CyclicBarrierCompletionMethodExampleUnitTest {

   @Test
   public void whenCyclicBarrier_countTrips() {
      CyclicBarrierCompletionMethodExample ex = new CyclicBarrierCompletionMethodExample(7, 20);
      int lineCount = ex.countTrips();
      assertEquals(2, lineCount);
   }
}
