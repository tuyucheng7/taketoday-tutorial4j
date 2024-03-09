package cn.tuyucheng.taketoday.timeago.version7;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

public class TimeAgoCalculatorUnitTest {

   private long getCurrentTime() {
      Calendar calendar = Calendar.getInstance();
      calendar.set(2020, 1, 1, 12, 0, 0);
      return calendar.getTimeInMillis();
      // We return a fixed date and time in order to avoid issues related to getting time from local in unit tests.
      // return System.currentTimeMillis();
   }

   @Test
   public void timeAgoByTimeGranularityTest() {
      long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
      Assertions.assertEquals("5 seconds ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * 1000)), TimeGranularity.SECONDS));
      Assertions.assertEquals("5 minutes ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * 60 * 1000)), TimeGranularity.MINUTES));
      Assertions.assertEquals("5 hours ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * 60 * 60 * 1000)), TimeGranularity.HOURS));
      Assertions.assertEquals("5 days ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS)), TimeGranularity.DAYS));
      Assertions.assertEquals("5 months ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 30)), TimeGranularity.MONTHS));
      Assertions.assertEquals("5 weeks ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 7)), TimeGranularity.WEEKS));
      Assertions.assertEquals("5 years ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 365)), TimeGranularity.YEARS));
      Assertions.assertEquals("5 decades ago", TimeAgoCalculator.calculateTimeAgoByTimeGranularity(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 365 * 10)), TimeGranularity.DECADES));
   }

   @Test
   public void humanFriendlyTimeAgoTest() {
      long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
      Assertions.assertEquals("moments ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * 1000))));
      Assertions.assertEquals("several minutes ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * 60 * 1000))));
      Assertions.assertEquals("several hours ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * 60 * 60 * 1000))));
      Assertions.assertEquals("several days ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS))));
      Assertions.assertEquals("several months ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 30))));
      Assertions.assertEquals("several weeks ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (3 * DAY_IN_MILLIS * 7))));
      Assertions.assertEquals("several years ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 365))));
      Assertions.assertEquals("several decades ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 365 * 10))));
   }

   @Test
   public void calculateExactTimeAgoWithJodaTimeTest() {
      Assertions.assertEquals("5 hours and 15 minutes and 3 seconds", TimeAgoCalculator.calculateExactTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * 60 * 60 * 1000 + 15 * 60 * 1000 + 3 * 1000))));
      Assertions.assertEquals("5 hours and 1 minute and 1 second", TimeAgoCalculator.calculateExactTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * 60 * 60 * 1000 + 1 * 60 * 1000 + 1 * 1000))));
      Assertions.assertEquals("2 days and 1 minute and 1 second", TimeAgoCalculator.calculateExactTimeAgoWithJodaTime(new Date(getCurrentTime() - (2 * 24 * 60 * 60 * 1000 + 1 * 60 * 1000 + 1 * 1000))));
   }

   @Test
   public void calculateHumanFriendlyTimeAgoWithJodaTimeTest() {
      long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
      Assertions.assertEquals("moments ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * 1000))));
      Assertions.assertEquals("several minutes ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * 60 * 1000))));
      Assertions.assertEquals("several hours ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * 60 * 60 * 1000))));
      Assertions.assertEquals("several days ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS))));
      Assertions.assertEquals("several months ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 30))));
      Assertions.assertEquals("several weeks ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (3 * DAY_IN_MILLIS * 7))));
      Assertions.assertEquals("several years ago", TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5 * DAY_IN_MILLIS * 365))));
   }

}
