package cn.tuyucheng.taketoday.datetime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

public class CalendarUtilsUnitTest {

   @Test
   public void givenDateAndDaysToAdd_thenCalendarIsCorrectlyReturned() throws ParseException {
      Date initialDate = DateUtils.getDate("2020/01/01", "yyyy/MM/dd");
      Date expectedDate = DateUtils.getDate("2020/01/11", "yyyy/MM/dd");
      Assertions.assertEquals(expectedDate, CalendarUtils.getPlusDays(initialDate, 10).getTime());
   }
}