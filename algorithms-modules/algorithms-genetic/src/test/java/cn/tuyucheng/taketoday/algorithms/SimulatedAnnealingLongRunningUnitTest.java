package cn.tuyucheng.taketoday.algorithms;


import cn.tuyucheng.taketoday.algorithms.ga.annealing.SimulatedAnnealing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedAnnealingLongRunningUnitTest {

   @Test
   void testSimulateAnnealing() {
      assertTrue(SimulatedAnnealing.simulateAnnealing(10, 1000, 0.9) > 0);
   }

}
