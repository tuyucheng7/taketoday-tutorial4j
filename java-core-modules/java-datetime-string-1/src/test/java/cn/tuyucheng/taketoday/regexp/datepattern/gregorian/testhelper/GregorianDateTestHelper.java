package cn.tuyucheng.taketoday.regexp.datepattern.gregorian.testhelper;

import cn.tuyucheng.taketoday.regexp.datepattern.DateMatcher;
import org.junit.jupiter.api.Assertions;

public class GregorianDateTestHelper {

   private final DateMatcher matcher;

   public GregorianDateTestHelper(DateMatcher matcher) {
      this.matcher = matcher;
   }

   public void assertFormat() {
      Assertions.assertTrue(matcher.matches("2017-12-31"));
      Assertions.assertTrue(matcher.matches("2018-01-01"));

      Assertions.assertFalse(matcher.matches("2018-02"));
      Assertions.assertFalse(matcher.matches("2018-02-01-01"));
      Assertions.assertFalse(matcher.matches("2018-02-XX"));
      Assertions.assertFalse(matcher.matches(" 2018-02-01"));
      Assertions.assertFalse(matcher.matches("2018-02-01 "));
      Assertions.assertFalse(matcher.matches("2020/02/28"));
      Assertions.assertFalse(matcher.matches("2020.02.29"));
   }

   public void assertRange() {
      Assertions.assertTrue(matcher.matches("1900-01-01"));
      Assertions.assertTrue(matcher.matches("2205-05-25"));
      Assertions.assertTrue(matcher.matches("2999-12-31"));

      Assertions.assertFalse(matcher.matches("1899-12-31"));
      Assertions.assertFalse(matcher.matches("2018-05-35"));
      Assertions.assertFalse(matcher.matches("2018-13-05"));
      Assertions.assertFalse(matcher.matches("3000-01-01"));
      Assertions.assertFalse(matcher.matches("3200-02-29"));
   }

   public void assertFebruary29th() {
      Assertions.assertTrue(matcher.matches("1904-02-29"));
      Assertions.assertTrue(matcher.matches("1996-02-29"));
      Assertions.assertTrue(matcher.matches("2000-02-29"));
      Assertions.assertTrue(matcher.matches("2400-02-29"));
      Assertions.assertTrue(matcher.matches("2800-02-29"));
      Assertions.assertTrue(matcher.matches("2020-02-29"));
      Assertions.assertTrue(matcher.matches("2024-02-29"));
      Assertions.assertTrue(matcher.matches("2028-02-29"));

      Assertions.assertFalse(matcher.matches("1900-02-29"));
      Assertions.assertFalse(matcher.matches("1999-02-29"));
      Assertions.assertFalse(matcher.matches("2017-02-29"));
      Assertions.assertFalse(matcher.matches("2018-02-29"));
      Assertions.assertFalse(matcher.matches("2019-02-29"));
      Assertions.assertFalse(matcher.matches("2100-02-29"));
      Assertions.assertFalse(matcher.matches("2200-02-29"));
      Assertions.assertFalse(matcher.matches("2300-02-29"));
   }

   public void assertFebruaryGeneralDates() {
      Assertions.assertTrue(matcher.matches("2018-02-01"));
      Assertions.assertTrue(matcher.matches("2019-02-13"));
      Assertions.assertTrue(matcher.matches("2020-02-25"));

      Assertions.assertFalse(matcher.matches("2000-02-30"));
      Assertions.assertFalse(matcher.matches("2400-02-62"));
      Assertions.assertFalse(matcher.matches("2420-02-94"));
   }

   public void assertMonthsOf30Days() {
      Assertions.assertTrue(matcher.matches("2018-04-30"));
      Assertions.assertTrue(matcher.matches("2019-06-30"));
      Assertions.assertTrue(matcher.matches("2020-09-30"));
      Assertions.assertTrue(matcher.matches("2021-11-30"));

      Assertions.assertTrue(matcher.matches("2022-04-02"));
      Assertions.assertTrue(matcher.matches("2023-06-14"));
      Assertions.assertTrue(matcher.matches("2024-09-26"));

      Assertions.assertFalse(matcher.matches("2018-04-31"));
      Assertions.assertFalse(matcher.matches("2019-06-31"));
      Assertions.assertFalse(matcher.matches("2020-09-31"));
      Assertions.assertFalse(matcher.matches("2021-11-31"));

      Assertions.assertFalse(matcher.matches("2022-04-32"));
      Assertions.assertFalse(matcher.matches("2023-06-64"));
      Assertions.assertFalse(matcher.matches("2024-09-96"));
   }

   public void assertMonthsOf31Dates() {
      Assertions.assertTrue(matcher.matches("2018-01-31"));
      Assertions.assertTrue(matcher.matches("2019-03-31"));
      Assertions.assertTrue(matcher.matches("2020-05-31"));
      Assertions.assertTrue(matcher.matches("2021-07-31"));
      Assertions.assertTrue(matcher.matches("2022-08-31"));
      Assertions.assertTrue(matcher.matches("2023-10-31"));
      Assertions.assertTrue(matcher.matches("2024-12-31"));

      Assertions.assertTrue(matcher.matches("2025-01-03"));
      Assertions.assertTrue(matcher.matches("2026-03-15"));
      Assertions.assertTrue(matcher.matches("2027-05-27"));

      Assertions.assertFalse(matcher.matches("2018-01-32"));
      Assertions.assertFalse(matcher.matches("2019-03-64"));
      Assertions.assertFalse(matcher.matches("2020-05-96"));
   }
}
