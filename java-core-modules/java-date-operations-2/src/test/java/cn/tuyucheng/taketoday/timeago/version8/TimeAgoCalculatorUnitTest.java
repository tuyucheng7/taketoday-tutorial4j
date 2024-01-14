package cn.tuyucheng.taketoday.timeago.version8;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;

public class TimeAgoCalculatorUnitTest {

   private LocalDateTime getCurrentTime() {
      LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 12, 0, 0);
      return localDateTime.atZone(ZoneId.systemDefault())
            .toLocalDateTime();
      // We return a fixed date and time in order to avoid issues related to getting time from local in unit tests.
      // return LocalDateTime.now(zone);
   }

   @Test
   public void calculateTimeAgoWithPeriodAndDurationTest() {
      Assertions.assertEquals("moments ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime(), ZoneId.systemDefault()));
      Assertions.assertEquals("several seconds ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime().minus(Duration.ofSeconds(5)), ZoneId.systemDefault()));
      Assertions.assertEquals("several minutes ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime().minus(Duration.ofMinutes(5)), ZoneId.systemDefault()));
      Assertions.assertEquals("several hours ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime().minus(Duration.ofHours(5)), ZoneId.systemDefault()));
      Assertions.assertEquals("several days ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime().minus(Period.ofDays(5)), ZoneId.systemDefault()));
      Assertions.assertEquals("several months ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime().minus(Period.ofMonths(5)), ZoneId.systemDefault()));
      Assertions.assertEquals("several years ago", TimeAgoCalculator.calculateTimeAgoWithPeriodAndDuration(getCurrentTime().minus(Period.ofYears(5)), ZoneId.systemDefault()));
   }
}
