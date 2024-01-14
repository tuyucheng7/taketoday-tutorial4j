package cn.tuyucheng.taketoday.helpervsutilityclasses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyUtilityClassUnitTest {

   @Test
   void whenUsingUtilityMethods_thenAccessMethodsViaClassName() {
      assertEquals("INIUBONG", MyUtilityClass.returnUpperCase("iniubong"));
      assertEquals("accra", MyUtilityClass.returnLowerCase("AcCrA"));
   }
}