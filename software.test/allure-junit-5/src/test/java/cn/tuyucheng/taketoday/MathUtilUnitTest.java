package cn.tuyucheng.taketoday;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Calculator Demo")
@Epic("Simple calculator unit test")
public class MathUtilUnitTest {

   @Description("When the given number is even, calling isEven should return true")
   @ParameterizedTest(name = "#{index} - {0} should be even")
   @ValueSource(ints = {8, 4, 2, 6, 10})
   void isEven_ShouldReturnTrueForEvenNumbers(int arg) {
      assertTrue(MathUtil.isEven(arg));
   }

   @Description("When the given number is prime, calling isPrime should return true")
   @ParameterizedTest(name = "#{index} - {0} should be prime")
   @ValueSource(ints = {3, 5, 11, 23})
   void isPrime_ShouldReturnTrueForPrimeNumbers(int arg) {
      assertTrue(MathUtil.isPrime(arg));
   }

   @Description("When the given number is not prime, calling isPrime should return false")
   @ParameterizedTest(name = "#{index} - {0} should not be prime")
   @ValueSource(ints = {6, 8, 9})
   void isCombination_ShouldReturnFalseForPrimeNumbers(int arg) {
      assertFalse(MathUtil.isPrime(arg));
   }

   @ParameterizedTest(name = "#{index} - Test with {0} + {1} = {2}")
   @CsvSource({"1,2,3", "3,4,7", "-1,-1,-2", "0,-1,-1"})
   void givenTwoNumbers_whenAddition_shouldCorrect(int num1, int num2, int expected) {
      assertEquals(MathUtil.add(num1, num2), expected);
   }

   @ParameterizedTest(name = "#{index} - Test with {0} * {1} = {2}")
   @CsvSource({"1,2,2", "3,4,12", "-1,-1,1", "0,-1,0"})
   void givenTwoNumbers_whenMultiple_shouldCorrect(int num1, int num2, int expected) {
      assertEquals(MathUtil.multiple(num1, num2), expected);
   }

   @ParameterizedTest(name = "#{index} - Test with {0} / {1} = {2}")
   @CsvSource({"1,2,0", "4,2,2", "-1,-1,1", "0,-1,0"})
   void givenTwoNumbers_whenDivision_shouldCorrect(int num1, int num2, int expected) {
      assertEquals(MathUtil.division(num1, num2), expected);
   }
}