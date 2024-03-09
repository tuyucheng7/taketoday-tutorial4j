package cn.tuyucheng.taketoday.replace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceStringUnitTest {
   private final String ORIGINAL_STRING = "This is 'Tuyucheng' tutorial.";
   private final String EXPECTED_STRING = "This is \\'Tuyucheng\\' tutorial.";

   @Test
   public void givenString_thenReplaceUsinReplaceAllMethod() {
      String modifiedString = ORIGINAL_STRING.replaceAll("'", "\\\\'");
      assertEquals(EXPECTED_STRING, modifiedString);
   }

   @Test
   public void givenString_thenReplaceUsinReplaceMethod() {
      String modifiedString = ORIGINAL_STRING.replace("'", "\\'");
      assertEquals(EXPECTED_STRING, modifiedString);
   }
}