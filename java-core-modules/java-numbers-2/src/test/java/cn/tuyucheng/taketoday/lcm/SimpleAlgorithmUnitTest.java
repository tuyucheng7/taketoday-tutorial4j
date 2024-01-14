package cn.tuyucheng.taketoday.lcm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.lcm.SimpleAlgorithm.lcm;

public class SimpleAlgorithmUnitTest {

   @Test
   public void testLCM() {
      Assertions.assertEquals(36, lcm(12, 18));
   }

}
