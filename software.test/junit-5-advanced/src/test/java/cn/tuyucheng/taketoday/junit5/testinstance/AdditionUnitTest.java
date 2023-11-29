package cn.tuyucheng.taketoday.junit5.testinstance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdditionUnitTest {

   private int sum = 1;

   @Test
   void addingTwoToSumReturnsThree() {
      sum += 2;
      assertEquals(3, sum);
   }

   @Test
   void addingThreeToSumReturnsFour() {
      sum += 3;
      assertEquals(4, sum);
   }
}