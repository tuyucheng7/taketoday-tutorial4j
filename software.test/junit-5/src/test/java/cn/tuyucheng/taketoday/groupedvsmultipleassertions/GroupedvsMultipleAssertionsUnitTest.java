package cn.tuyucheng.taketoday.groupedvsmultipleassertions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupedvsMultipleAssertionsUnitTest {

   @Test
   void givenAssertAll_whenSingleAssertStatementFails_thenRestWillStillBeExecuted() {
      User user = new User("tuyucheng", "support@tuyucheng.com", true);
      assertAll("Grouped Assertions of User",
            () -> assertEquals("tuyucheng", user.getUsername(), "Username should be tuyucheng"),
            () -> assertEquals("support@tuyucheng.com", user.getEmail(), "Email should be support@tuyucheng.com"),
            () -> assertTrue(user.getActivated(), "User should be activated")
      );
   }

   @Test
   void givenMultipleAssertions_whenSingleAssertStatementFails_thenRestWillNotBeExecuted() {
      User user = new User("tuyucheng", "support@tuyucheng.com", false);
      assertEquals("tuyucheng", user.getUsername(), "Username should be tuyucheng");
      assertEquals("support@tuyucheng.com", user.getEmail(), "Email should be support@tuyucheng.com");
      assertFalse(user.getActivated(), "User should not be activated");
   }
}