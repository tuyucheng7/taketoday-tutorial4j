package cn.tuyucheng.taketoday.interview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringChangeCaseUnitTest {
   @Test
   public void givenString_whenChangingToUppercase_thenCaseChanged() {
      String s = "Welcome to Tuyucheng!";
      assertEquals("WELCOME TO BAELDUNG!", s.toUpperCase());
   }


   @Test
   public void givenString_whenChangingToLowerrcase_thenCaseChanged() {
      String s = "Welcome to Tuyucheng!";
      assertEquals("welcome to tuyucheng!", s.toLowerCase());
   }
}
