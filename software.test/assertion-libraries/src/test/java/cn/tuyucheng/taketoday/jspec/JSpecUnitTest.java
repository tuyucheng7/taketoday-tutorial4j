package cn.tuyucheng.taketoday.jspec;

import org.javalite.test.jspec.ExceptionExpectation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.javalite.test.jspec.JSpec.*;

class JSpecUnitTest {

   @Test
   void onePlusTwo_shouldEqualThree() {
      $(1 + 2).shouldEqual(3);
      a(1 + 2).shouldEqual(3);
      the(1 + 2).shouldEqual(3);
      it(1 + 2).shouldEqual(3);
   }

   @Test
   void messageShouldContainJSpec() {
      String message = "Welcome to JSpec demo";
      // The message should not be empty
      the(message).shouldNotBe("empty");
      // The message should contain JSpec
      the(message).shouldContain("JSpec");
   }

   void colorsListShouldContainRed() {
      List<String> colorsList = Arrays.asList("red", "green", "blue");
      $(colorsList).shouldContain("red");
   }

   void guessedNumberShouldEqualHiddenNumber() {
      Integer guessedNumber = 11;
      Integer hiddenNumber = 11;

      $(guessedNumber).shouldEqual(hiddenNumber);
      $(guessedNumber).shouldNotBeTheSameAs(hiddenNumber);
   }

   @Test
   void dividingByThero_shouldThrowArithmeticException() {
      expect(new ExceptionExpectation<ArithmeticException>(ArithmeticException.class) {
         @Override
         public void exec() throws ArithmeticException {
            System.out.println(1 / 0);
         }
      });
   }
}