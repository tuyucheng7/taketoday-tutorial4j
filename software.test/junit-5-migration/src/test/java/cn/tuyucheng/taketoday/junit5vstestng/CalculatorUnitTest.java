package cn.tuyucheng.taketoday.junit5vstestng;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorUnitTest {

   @Test
   public void whenDividerIsZero_thenDivideByZeroExceptionIsThrown() {
      Calculator calculator = new Calculator();

      assertThrows(DivideByZeroException.class, () -> calculator.divide(10, 0));
   }

   @Test
   public void whenDividerIsNotZero_thenShouldReturnCorrectResult() {
      Calculator calculator = new Calculator();

      assertEquals(5, calculator.divide(10, 2));
   }
}