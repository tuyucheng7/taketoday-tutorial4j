package cn.tuyucheng.taketoday.jvmtimezone;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModifyDefaultTimezoneUnitTest {

   @Test
   public void givenDefaultTimezoneSet_thenDateTimezoneIsCorrect() {
      TimeZone.setDefault(TimeZone.getTimeZone("Portugal"));
      Calendar calendar = Calendar.getInstance();
      assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("Portugal"));
   }

}
