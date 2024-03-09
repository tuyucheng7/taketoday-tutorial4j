package cn.tuyucheng.taketoday.zonedatetime;

import org.junit.jupiter.api.Test;

import java.time.OffsetTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OffsetTimeExampleUnitTest {

   OffsetTimeExample offsetTimeExample = new OffsetTimeExample();

   @Test
   public void givenZoneOffset_whenGetCurrentTime_thenResultHasZone() {
      String offset = "+02:00";
      OffsetTime time = offsetTimeExample.getCurrentTimeByZoneOffset(offset);

      assertTrue(time.getOffset()
            .equals(ZoneOffset.of(offset)));
   }
}
