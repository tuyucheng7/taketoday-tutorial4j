package cn.tuyucheng.taketoday.datetime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

public class DateUtilsUnitTest {

   @Test
   public void givenDateAndPattern_thenDateIsCorrectlyReturned() throws ParseException {
      long milliseconds = new Date(2020 - 1900, 0, 1).getTime();
      Assertions.assertEquals(DateUtils.getDate(milliseconds), DateUtils.getDate("2020/01/01", "yyyy/MM/dd"));
   }
}