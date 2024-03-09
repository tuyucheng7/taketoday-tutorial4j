package cn.tuyucheng.taketoday.jvmtimezone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModifyTimezonePropertyUnitTest {

   @BeforeEach
   public void setup() {
      System.setProperty("user.timezone", "Asia/Kolkata");
      TimeZone.setDefault(null);
   }

   @AfterEach
   public void teardown() {
      System.clearProperty("user.timezone");
   }

   @Test
   public void givenTimezonePropertySet_thenDateTimezoneIsCorrect() {
      Calendar calendar = Calendar.getInstance();
      assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("Asia/Kolkata"));
   }

}