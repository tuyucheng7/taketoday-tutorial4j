package cn.tuyucheng.taketoday.migration.junit5;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AssertionsExampleUnitTest {

   @Test
   @Disabled
   void shouldFailBecauseTheNumbersAreNotEqual() {
      Assertions.assertEquals(3, 2, "Numbers are not equal!");
   }

   @Test
   @Disabled
   void shouldFailBecauseItsNotTrue_overloading() {
      Assertions.assertTrue(() -> {
         return false;
      }, () -> "It's not true!");
   }

   @Test
   void shouldAssertAllTheGroup() {
      List<Integer> list = Arrays.asList(1, 2, 3);

      Assertions.assertAll("List is not incremental",
            () -> Assertions.assertEquals(1, list.get(0).intValue()),
            () -> Assertions.assertEquals(2, list.get(1).intValue()),
            () -> Assertions.assertEquals(3, list.get(2).intValue()));
   }
}