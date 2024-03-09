package cn.tuyucheng.taketoday.datetime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class UseLocalTimeUnitTest {

   private UseLocalTime useLocalTime = new UseLocalTime();

   @Test
   public void givenValues_whenUsingFactoryOf_thenLocalTime() {
      Assertions.assertEquals("07:07:07", useLocalTime.getLocalTimeUsingFactoryOfMethod(7, 7, 7)
            .toString());
   }

   @Test
   public void givenValues_whenUsingFactoryOfWithoutSeconds_thenLocalTime() {
      Assertions.assertEquals("07:07", useLocalTime.getLocalTimeUsingFactoryOfMethod(7, 7)
            .toString());
   }

   @Test
   public void givenString_whenUsingParse_thenLocalTime() {
      Assertions.assertEquals("06:30", useLocalTime.getLocalTimeUsingParseMethod("06:30")
            .toString());
   }

   @Test
   public void givenTime_whenAddHour_thenLocalTime() {
      Assertions.assertEquals("07:30", useLocalTime.addAnHour(LocalTime.of(6, 30))
            .toString());
   }

   @Test
   public void getHourFromLocalTime() {
      Assertions.assertEquals(1, useLocalTime.getHourFromLocalTime(LocalTime.of(1, 1)));
   }

   @Test
   public void getLocalTimeWithMinuteSetToValue() {
      Assertions.assertEquals(LocalTime.of(10, 20), useLocalTime.getLocalTimeWithMinuteSetToValue(LocalTime.of(10, 10), 20));
   }
}
