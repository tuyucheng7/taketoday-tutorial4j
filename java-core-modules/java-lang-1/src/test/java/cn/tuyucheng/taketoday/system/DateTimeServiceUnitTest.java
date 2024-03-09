package cn.tuyucheng.taketoday.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateTimeServiceUnitTest {

   @Test
   public void givenClass_whenCalledMethods_thenNotNullInResult() {
      DateTimeService dateTimeService = new DateTimeService();

      Assertions.assertNotNull(dateTimeService.nowPlusOneHour());
      Assertions.assertNotNull(dateTimeService.nowPrettyPrinted());
   }
}
