package cn.tuyucheng.taketoday.systemrules;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.junit.Assert.assertEquals;

class SystemInUnitTest {

   @Test
   void givenTwoNames_whenSystemInMock_thenNamesJoinedTogether() throws Exception {
      withTextFromSystemIn("Jonathan", "Cook").execute(() -> {
         assertEquals("Names should be concatenated", "Jonathan Cook", getFullname());
      });
   }

   private String getFullname() {
      try (Scanner scanner = new Scanner(System.in)) {
         String firstName = scanner.next();
         String surname = scanner.next();
         return String.join(" ", firstName, surname);
      }
   }
}