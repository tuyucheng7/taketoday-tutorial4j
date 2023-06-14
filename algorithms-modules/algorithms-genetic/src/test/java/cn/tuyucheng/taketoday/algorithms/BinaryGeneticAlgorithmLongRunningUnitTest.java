package cn.tuyucheng.taketoday.algorithms;


import cn.tuyucheng.taketoday.algorithms.ga.binary.SimpleGeneticAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BinaryGeneticAlgorithmLongRunningUnitTest {

   @Test
   void testGA() {
      SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm();
      assertTrue(ga.runAlgorithm(50, "1011000100000100010000100000100111001000000100000100000000001111"));
   }

}
