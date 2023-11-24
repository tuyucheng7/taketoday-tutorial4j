package cn.tuyucheng.taketoday.recordswithjpa.repository;

import cn.tuyucheng.taketoday.recordswithjpa.RecordsAsJpaEmbeddableIntegrationTest;
import cn.tuyucheng.taketoday.recordswithjpa.embeddable.Author;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddableBookRepositoryIntegrationTest extends RecordsAsJpaEmbeddableIntegrationTest {

   @Test
   void findBookByAuthor() {
      assertEquals(2, bookRepository.findBookByAuthor(new Author("J.R.R.", "Tolkien")).size());
   }
}