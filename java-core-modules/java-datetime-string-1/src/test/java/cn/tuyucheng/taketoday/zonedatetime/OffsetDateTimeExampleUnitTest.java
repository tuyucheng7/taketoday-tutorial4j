package cn.tuyucheng.taketoday.zonedatetime;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OffsetDateTimeExampleUnitTest {

   OffsetDateTimeExample offsetDateTimeExample = new OffsetDateTimeExample();

   @Test
   public void givenZoneOffset_whenGetCurrentTime_thenResultHasZone() {
      String offset = "+02:00";
      OffsetDateTime time = offsetDateTimeExample.getCurrentTimeByZoneOffset(offset);

      assertTrue(time.getOffset()
            .equals(ZoneOffset.of(offset)));
   }
}
