package cn.tuyucheng.taketoday.disabled;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DisabledUntil;

public class TemporarilyDisabledUnitTest {

   @Test
   @DisabledUntil(date = "2023-04-22")
   void disabledTest() {
      // Test will be skipped if it's 2023-04-21 or earlier
   }

   @Test
   @DisabledUntil(reason = "The remote server won't be ready until next year", date = "2023-04-22")
   void disabledTestWithReason() {
      // Test will be skipped if it's 2023-04-21 or earlier
   }
}