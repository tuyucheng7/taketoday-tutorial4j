package cn.tuyucheng.taketoday.convert;

import org.joda.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class ConvertDateTimeUnitTest {

   public final long millis = 1556175797428L;

   @Test
   public void givenLocalDateTime_WhenToEpochMillis_ThenMillis() {
      ZoneId id = ZoneId.systemDefault();

      LocalDateTime localDateTime =
            LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(millis), id);

      ZonedDateTime zdt = ZonedDateTime.of(localDateTime, id);

      Assertions.assertEquals(millis, zdt.toInstant().toEpochMilli());
   }

   @Test
   public void givenJava8Instant_WhenGToEpochMillis_ThenMillis() {
      java.time.Instant instant = java.time.Instant.ofEpochMilli(millis);
      Assertions.assertEquals(millis, instant.toEpochMilli());
   }

   @Test
   public void givenDate_WhenGetTime_ThenMillis() {
      Date date = new Date(millis);
      Assertions.assertEquals(millis, date.getTime());
   }

   @Test
   public void givenCalendar_WhenGetTimeInMillis_ThenMillis() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date(millis));
      Assertions.assertEquals(millis, calendar.getTimeInMillis());
   }

   @Test
   public void givenJodaInstant_WhenGetMillis_ThenMillis() {
      Instant jodaInstant = Instant.ofEpochMilli(millis);
      Assertions.assertEquals(millis, jodaInstant.getMillis());
   }

   @Test
   public void givenJODADateTime_WhenGetMillis_ThenMillis() {
      org.joda.time.DateTime jodaDateTime = new org.joda.time.DateTime(millis);
      Assertions.assertEquals(millis, jodaDateTime.getMillis());
   }
}
