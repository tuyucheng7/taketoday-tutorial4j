package cn.tuyucheng.taketoday.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class SystemPropertiesUnitTest {

   @Test
   public void givenSystem_whenCalledGetProperty_thenReturnPropertyinResult() {
      Assertions.assertNotNull(System.getProperty("java.vm.vendor"));
   }

   @Test
   public void givenSystem_whenCalledSetProperty_thenSetPropertyasResult() {

      // set a particular property
      System.setProperty("abckey", "abcvaluefoo");
      Assertions.assertEquals("abcvaluefoo", System.getProperty("abckey"));
   }

   @Test
   public void givenSystem_whenCalledClearProperty_thenDeletePropertyasResult() {

      // Delete a property
      System.clearProperty("abckey");
      Assertions.assertNull(System.getProperty("abckey"));
   }

   @Test
   public void givenSystem_whenCalledGetPropertyDefaultValue_thenReturnPropertyinResult() {

      System.clearProperty("dbHost");
      String myKey = System.getProperty("dbHost", "db.host.com");
      Assertions.assertEquals("db.host.com", myKey);
   }

   @Test
   public void givenSystem_whenCalledGetProperties_thenReturnPropertiesinResult() {
      Properties properties = System.getProperties();

      Assertions.assertNotNull(properties);
   }

   @Test
   @Disabled
   public void givenSystem_whenCalledClearProperties_thenDeleteAllPropertiesasResult() {

      // Clears all system properties. Use with care!
      System.getProperties().clear();

      Assertions.assertTrue(System.getProperties().isEmpty());
   }
}
