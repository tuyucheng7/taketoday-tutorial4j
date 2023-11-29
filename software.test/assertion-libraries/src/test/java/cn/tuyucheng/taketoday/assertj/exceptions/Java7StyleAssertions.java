package cn.tuyucheng.taketoday.assertj.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class Java7StyleAssertions {

   @Test
   void whenDividingByZero_thenArithmeticException() {
      try {
         int numerator = 10;
         int denominator = 0;
         int quotient = numerator / denominator;
         fail("ArithmeticException expected because dividing by zero yields an ArithmeticException.");
         failBecauseExceptionWasNotThrown(ArithmeticException.class);
      } catch (Exception e) {
         assertThat(e).hasMessage("/ by zero");
         assertThat(e).isInstanceOf(ArithmeticException.class);
      }
   }
}