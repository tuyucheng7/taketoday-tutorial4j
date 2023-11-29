package cn.tuyucheng.taketoday.cucumber.tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddingNumberUnitTest {

   private final Calculator calculator = new Calculator();

   @Test
   void addTwoPositiveNumbers() {
      int expectedResult = 30;
      int actualResult = calculator.add(10, 20);
      assertEquals(expectedResult, actualResult, "the sum of two positive numbers is incorrect");
   }

   @Test
   void addTwoNegativeNumbers() {
      int expectedResult = -30;
      int actualResult = calculator.add(-10, -20);
      assertEquals(expectedResult, actualResult, "the sum of two negative numbers is incorrect");
   }
}