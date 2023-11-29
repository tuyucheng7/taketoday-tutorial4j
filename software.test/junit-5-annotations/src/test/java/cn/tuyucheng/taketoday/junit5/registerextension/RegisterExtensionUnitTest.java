package cn.tuyucheng.taketoday.junit5.registerextension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test demonstrates the use of the same extension in two ways.
 * <p>
 * 1. Once as instance level field: Only method level callbacks are called.
 * <p>
 * 2. Once as class level static field: All methods are called.
 */
class RegisterExtensionUnitTest {

   @RegisterExtension
   static RegisterExtensionSampleExtension staticExtension = new RegisterExtensionSampleExtension("static version");

   @RegisterExtension
   RegisterExtensionSampleExtension instanceLevelExtension = new RegisterExtensionSampleExtension("instance version");

   @Test
   void demoTest() {
      assertEquals("instance version", instanceLevelExtension.getType());
   }
}