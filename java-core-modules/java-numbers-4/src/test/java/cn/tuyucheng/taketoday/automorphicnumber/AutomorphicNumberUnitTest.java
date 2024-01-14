package cn.tuyucheng.taketoday.automorphicnumber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomorphicNumberUnitTest {

   @Test
   void givenANumber_whenPassed_thenShouldDetermineAutomorphicOrNot() {
      int number1 = 76; // automorphic
      int number2 = 16; // not automorphic
      assertTrue(AutomorphicNumber.isAutomorphicUsingLoop(number1));
      assertFalse(AutomorphicNumber.isAutomorphicUsingLoop(number2));
      assertTrue(AutomorphicNumber.isAutomorphicUsingMath(number1));
      assertFalse(AutomorphicNumber.isAutomorphicUsingMath(number2));
   }
}
