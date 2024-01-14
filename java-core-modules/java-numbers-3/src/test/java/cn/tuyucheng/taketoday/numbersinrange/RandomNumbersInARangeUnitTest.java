package cn.tuyucheng.taketoday.numbersinrange;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomNumbersInARangeUnitTest {

   @Test
   public void givenTheRange1To10_andUsingMathRandom_thenExpectCorrectResult() {
      RandomNumbersInARange randomNumbersInARange = new RandomNumbersInARange();
      int number = randomNumbersInARange.getRandomNumber(1, 10);

      assertTrue(number >= 1);
      assertTrue(number < 10);
   }

   @Test
   public void givenTheRange1To10_andUsingRandomInts_thenExpectCorrectResult() {
      RandomNumbersInARange randomNumbersInARange = new RandomNumbersInARange();
      int number = randomNumbersInARange.getRandomNumberUsingInts(1, 10);

      assertTrue(number >= 1);
      assertTrue(number < 10);
   }

   @Test
   public void givenTheRange1To10_andUsingRandomNextInt_thenExpectCorrectResult() {
      RandomNumbersInARange randomNumbersInARange = new RandomNumbersInARange();
      int number = randomNumbersInARange.getRandomNumberUsingNextInt(1, 10);

      assertTrue(number >= 1);
      assertTrue(number < 10);
   }
}
