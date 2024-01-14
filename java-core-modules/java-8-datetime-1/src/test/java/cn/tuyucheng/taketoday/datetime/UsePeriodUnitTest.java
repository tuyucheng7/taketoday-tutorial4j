package cn.tuyucheng.taketoday.datetime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

public class UsePeriodUnitTest {
   UsePeriod usingPeriod = new UsePeriod();

   @Test
   public void givenPeriodAndLocalDate_thenCalculateModifiedDate() {
      Period period = Period.ofDays(1);
      LocalDate localDate = LocalDate.parse("2007-05-10");
      Assertions.assertEquals(localDate.plusDays(1), usingPeriod.modifyDates(localDate, period));
   }

   @Test
   public void givenDates_thenGetPeriod() {
      LocalDate localDate1 = LocalDate.parse("2007-05-10");
      LocalDate localDate2 = LocalDate.parse("2007-05-15");

      Assertions.assertEquals(Period.ofDays(5), usingPeriod.getDifferenceBetweenDates(localDate1, localDate2));
   }
}
