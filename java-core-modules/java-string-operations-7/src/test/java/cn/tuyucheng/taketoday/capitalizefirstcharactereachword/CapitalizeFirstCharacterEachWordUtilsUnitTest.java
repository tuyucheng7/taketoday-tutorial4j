package cn.tuyucheng.taketoday.capitalizefirstcharactereachword;

import org.apache.commons.text.WordUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CapitalizeFirstCharacterEachWordUtilsUnitTest {

   @Test
   void givenString_whenUsingCharacterToUpperCaseMethod_thenCapitalizeFirstCharacter() {
      String input = "hello tuyucheng visitors";

      Assertions.assertEquals("Hello Tuyucheng Visitors", CapitalizeFirstCharacterEachWordUtils.usingCharacterToUpperCaseMethod(input));
   }

   @Test
   void givenString_whenUsingSubstringMethod_thenCapitalizeFirstCharacter() {
      String input = "Hi, my name is azhrioun";

      assertEquals("Hi, My Name Is Azhrioun", CapitalizeFirstCharacterEachWordUtils.usingStringToUpperCaseMethod(input));
   }

   @Test
   void givenString_whenUsingStringUtilsClass_thenCapitalizeFirstCharacter() {
      String input = "life is short the world is wide";

      assertEquals("Life Is Short The World Is Wide", CapitalizeFirstCharacterEachWordUtils.usingStringUtilsClass(input));
   }

   @Test
   void givenString_whenUsingWordUtilsClass_thenCapitalizeFirstCharacter() {
      String input = "smile sunshine is good for your teeth";

      assertEquals("Smile Sunshine Is Good For Your Teeth", WordUtils.capitalizeFully(input));
   }

}
