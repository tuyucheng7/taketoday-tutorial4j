package cn.tuyucheng.taketoday.concurrent.cyclicbarrier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CyclicBarrierResetExampleUnitTest {

   @Test
   public void whenCyclicBarrier_reset() {
      CyclicBarrierResetExample ex = new CyclicBarrierResetExample(7, 20);
      int lineCount = ex.countWaits();
      assertTrue(lineCount > 7);
   }
}
