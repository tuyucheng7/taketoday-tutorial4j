package cn.tuyucheng.taketoday.core.nativekeyword;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DateTimeUtilsManualTest {

   private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DateTimeUtilsManualTest.class);

   @BeforeAll
   public static void setUpClass() {
      System.loadLibrary("msvcr100");
      System.loadLibrary("libgcc_s_sjlj-1");
      System.loadLibrary("libstdc++-6");
      System.loadLibrary("nativedatetimeutils");
   }

   @Test
   public void givenNativeLibsLoaded_thenNativeMethodIsAccessible() {
      DateTimeUtils dateTimeUtils = new DateTimeUtils();
      LOG.info("System time is : " + dateTimeUtils.getSystemTime());
      assertNotNull(dateTimeUtils.getSystemTime());
   }
}
