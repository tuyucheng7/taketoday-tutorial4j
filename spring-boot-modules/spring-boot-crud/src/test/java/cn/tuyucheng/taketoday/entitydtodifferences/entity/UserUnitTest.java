package cn.tuyucheng.taketoday.entitydtodifferences.entity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserUnitTest {

   @Test
   void whenUserInitialized_thenInitializedCorrectly() {
      // when
      Book book1 = new Book("Book1", "Author1");
      Book book2 = new Book("Book2", "Author2");
      User user = new User("John", "Doe", "123 Main St", Arrays.asList(book1, book2));
      // then
      assertNotNull(user);
      assertEquals("John", user.getFirstName());
      assertEquals("Doe", user.getLastName());
      assertEquals("123 Main St", user.getAddress());
      assertEquals(2, user.getBooks()
            .size());
   }

   @Test
   void givenUserOwningMultipleBooks_whenGetNameOfMostOwnedBook_thenComputedCorrectly() {
      // given
      Book book1 = new Book("Book1", "Author1");
      Book book2 = new Book("Book2", "Author2");
      Book book3 = new Book("Book2", "Author3");
      User user = new User("John", "Doe", "123 Main St", Arrays.asList(book1, book2, book3));
      // when
      String mostOwnedBook = user.getNameOfMostOwnedBook();
      // then
      assertEquals("Book2", mostOwnedBook);
   }

   @Test
   void givenUserWithNoBooks_henGetNameOfMostOwnedBook_thenReturnedNull() {
      // given
      User user = new User("John", "Doe", "123 Main St", Collections.emptyList());
      // when
      String mostOwnedBook = user.getNameOfMostOwnedBook();
      // then
      assertNull(mostOwnedBook);
   }
}