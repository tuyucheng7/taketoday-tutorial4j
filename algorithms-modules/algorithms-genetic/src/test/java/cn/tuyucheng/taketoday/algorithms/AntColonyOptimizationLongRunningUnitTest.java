package cn.tuyucheng.taketoday.algorithms;


import cn.tuyucheng.taketoday.algorithms.ga.ant_colony.AntColonyOptimization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AntColonyOptimizationLongRunningUnitTest {

   @Test
   void testGenerateRandomMatrix() {
      AntColonyOptimization antTSP = new AntColonyOptimization(5);
      assertNotNull(antTSP.generateRandomMatrix(5));
   }

   @Test
   void testStartAntOptimization() {
      AntColonyOptimization antTSP = new AntColonyOptimization(5);
      assertNotNull(antTSP.solve());
   }

}
