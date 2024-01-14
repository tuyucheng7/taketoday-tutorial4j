package cn.tuyucheng.taketoday.java14.newfeatues;

import org.junit.jupiter.api.Test;

public class RecordUnitTest {

   @SuppressWarnings("preview")
   public record User(int id, String password) {
   }

   ;

   private User user1 = new User(0, "UserOne");

   @Test
   public void givenRecord_whenObjInitialized_thenValuesCanBeFetchedWithGetters() {

      assertEquals(0, user1.id());
      assertEquals("UserOne", user1.password());
   }

   @Test
   public void whenRecord_thenEqualsImplemented() {

      User user2 = user1;

      assertEquals(user1, user2);
   }

   @Test
   public void whenRecord_thenToStringImplemented() {

      assertTrue(user1.toString()
            .contains("UserOne"));
   }

}
