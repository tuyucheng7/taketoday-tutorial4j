package cn.tuyucheng.taketoday.lcm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EuclideanAlgorithmUnitTest {

   @Test
   public void testGCD() {
      Assertions.assertEquals(6, EuclideanAlgorithm.gcd(12, 18));
   }

   @Test
   public void testLCM() {
      Assertions.assertEquals(36, EuclideanAlgorithm.lcm(12, 18));
   }

   @Test
   public void testLCMForArray() {
      Assertions.assertEquals(15, EuclideanAlgorithm.lcmForArray(new int[]{3, 5, 15}));
   }

   @Test
   public void testLCMByLambdaForArray() {
      Assertions.assertEquals(15, EuclideanAlgorithm.lcmByLambda(new int[]{3, 5, 15}));
   }
}
