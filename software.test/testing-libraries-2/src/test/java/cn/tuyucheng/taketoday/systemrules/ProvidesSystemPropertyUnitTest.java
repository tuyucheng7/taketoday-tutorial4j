package cn.tuyucheng.taketoday.systemrules;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvidesSystemPropertyUnitTest {

   @BeforeAll
   static void setUpBeforeClass() throws Exception {
      System.setProperty("log_dir", "/tmp/tuyucheng/logs");
   }

   @Test
   void givenSetSystemProperty_whenGetLogDir_thenLogDirIsProvidedSuccessfully() throws Exception {
      restoreSystemProperties(() -> {
         System.setProperty("log_dir", "test/resources");
         assertEquals("test/resources", System.getProperty("log_dir"), "log_dir should be provided");
      });

      assertEquals("/tmp/tuyucheng/logs", System.getProperty("log_dir"), "log_dir should be provided");
   }
}