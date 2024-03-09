package cn.tuyucheng.taketoday.regexp.datepattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormattedDateMatcherUnitTest {

   private DateMatcher matcher = new FormattedDateMatcher();

   @Test
   public void whenUsingFormattedDateMatcher_thenFormatConstraintsSatisfied() {
      Assertions.assertTrue(matcher.matches("2017-12-31"));
      Assertions.assertTrue(matcher.matches("2018-01-01"));
      Assertions.assertTrue(matcher.matches("0000-00-00"));
      Assertions.assertTrue(matcher.matches("1029-99-72"));

      Assertions.assertFalse(matcher.matches("2018-01"));
      Assertions.assertFalse(matcher.matches("2018-01-01-01"));
      Assertions.assertFalse(matcher.matches("2018-01-XX"));
      Assertions.assertFalse(matcher.matches(" 2018-01-01"));
      Assertions.assertFalse(matcher.matches("2018-01-01 "));
      Assertions.assertFalse(matcher.matches("2018/01/01"));
   }
}
