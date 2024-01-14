package cn.tuyucheng.taketoday.leapyear;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

public class LeapYearUnitTest {

   // Before Java8
   @Test
   public void testLeapYearUsingGregorianCalendar() {
      Assertions.assertFalse(new GregorianCalendar().isLeapYear(2018));
   }

   // Java 8 and above
   @Test
   public void testLeapYearUsingJavaTimeYear() {
      Assertions.assertTrue(Year.isLeap(2012));
   }

   @Test
   public void testBCYearUsingJavaTimeYear() {
      Assertions.assertTrue(Year.isLeap(-4));
   }

   @Test
   public void testWrongLeapYearUsingJavaTimeYear() {
      Assertions.assertFalse(Year.isLeap(2018));
   }

   @Test
   public void testLeapYearInDateUsingJavaTimeYear() {
      LocalDate date = LocalDate.parse("2020-01-05", DateTimeFormatter.ISO_LOCAL_DATE);
      Assertions.assertTrue(Year.from(date).isLeap());
   }

}
