package cn.tuyucheng.taketoday.hamcrest.custommatchers;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.hamcrest.custommatchers.IsDivisibleBy.divisibleBy;
import static cn.tuyucheng.taketoday.hamcrest.custommatchers.IsOnlyDigits.onlyDigits;
import static cn.tuyucheng.taketoday.hamcrest.custommatchers.IsUppercase.uppercase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class HamcrestCustomUnitTest {

   @Test
   final void givenAString_whenIsOnlyDigits_thenCorrect() {
      String digits = "123";

      assertThat(digits, is(onlyDigits()));
   }

   @Test
   final void givenAString_whenIsNotOnlyDigits_thenCorrect() {
      String aphanumeric = "123ABC";

      assertThat(aphanumeric, is(not(onlyDigits())));
   }

   @Test
   final void givenAString_whenIsUppercase_thenCorrect() {
      String uppercaseWord = "HELLO";

      assertThat(uppercaseWord, is(uppercase()));
   }

   @Test
   final void givenAnEvenInteger_whenDivisibleByTwo_thenCorrect() {
      Integer ten = 10;
      Integer two = 2;

      assertThat(ten, is(divisibleBy(two)));
   }

   @Test
   final void givenAnOddInteger_whenNotDivisibleByTwo_thenCorrect() {
      Integer eleven = 11;
      Integer two = 2;

      assertThat(eleven, is(not(divisibleBy(two))));
   }
}