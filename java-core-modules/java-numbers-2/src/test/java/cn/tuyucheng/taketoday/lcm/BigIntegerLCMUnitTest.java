package cn.tuyucheng.taketoday.lcm;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class BigIntegerLCMUnitTest {

   @Test
   public void testLCM() {
      BigInteger number1 = new BigInteger("12");
      BigInteger number2 = new BigInteger("18");
      BigInteger expectedLCM = new BigInteger("36");
      Assertions.assertEquals(expectedLCM, BigIntegerLCM.lcm(number1, number2));
   }
}
