package cn.tuyucheng.taketoday.regexp.datepattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RangedDateMatcherUnitTest {

   private DateMatcher matcher = new RangedDateMatcher();

   @Test
   public void whenUsingRangedDateMatcher_thenFormatConstraintsSatisfied() {
      Assertions.assertFalse(matcher.matches("2018-01"));
      Assertions.assertFalse(matcher.matches("2018-01-01-01"));
      Assertions.assertFalse(matcher.matches("2018-01-XX"));
      Assertions.assertFalse(matcher.matches(" 2018-01-01"));
      Assertions.assertFalse(matcher.matches("2018-01-01 "));
      Assertions.assertFalse(matcher.matches("2018/01/01"));
   }

   @Test
   public void whenUsingRangedDateMatcher_thenRangeConstraintsSatisfied() {
      Assertions.assertTrue(matcher.matches("1900-01-01"));
      Assertions.assertTrue(matcher.matches("2018-02-31"));
      Assertions.assertTrue(matcher.matches("2999-12-31"));

      Assertions.assertFalse(matcher.matches("1899-12-31"));
      Assertions.assertFalse(matcher.matches("2018-05-35"));
      Assertions.assertFalse(matcher.matches("2018-13-05"));
      Assertions.assertFalse(matcher.matches("3000-01-01"));
   }
}
