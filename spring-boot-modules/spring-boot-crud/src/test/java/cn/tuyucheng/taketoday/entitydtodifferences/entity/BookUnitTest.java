package cn.tuyucheng.taketoday.entitydtodifferences.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookUnitTest {

   @Test
   void whenBookInitialized_thenInitializedCorrectly() {
      // when
      Book book = new Book("Book1", "Author1");
      // then
      assertNotNull(book);
      assertEquals("Book1", book.getName());
      assertEquals("Author1", book.getAuthor());
   }
}