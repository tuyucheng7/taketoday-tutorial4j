package cn.tuyucheng.taketoday.spring.insertableonly;

import cn.tuyucheng.taketoday.spring.insertableonly.repositorycheck.RepositoryCheckBook;
import cn.tuyucheng.taketoday.spring.insertableonly.repositorycheck.RepositoryCheckBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static cn.tuyucheng.taketoday.spring.insertableonly.ConstantHolder.NEW_TITLE;
import static cn.tuyucheng.taketoday.spring.insertableonly.ConstantHolder.TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
class RepositoryCheckBookRepositoryIntegrationTest {

   @Autowired
   private RepositoryCheckBookRepository repository;

   @BeforeEach
   void setup() {
      repository.deleteAll();
   }

   @Test
   void givenDatasourceWhenSaveBookThenBookPersisted() {
      RepositoryCheckBook newBook = new RepositoryCheckBook(TITLE);
      RepositoryCheckBook persistedBook = repository.persist(newBook);
      Long id = persistedBook.getId();
      assertThat(id).isNotNull();
      RepositoryCheckBook actualBook = getBookById(id);
      assertThat(actualBook.getTitle()).isEqualTo(TITLE);
      assertThat(actualBook.getId()).isEqualTo(id);
   }

   @Test
   void givenDatasourceWhenUpdateBookThenUpdatedIsIgnored() {
      RepositoryCheckBook book = new RepositoryCheckBook(TITLE);
      RepositoryCheckBook persistedBook = repository.persist(book);
      Long id = persistedBook.getId();
      persistedBook.setTitle(NEW_TITLE);
      repository.persist(persistedBook);
      Optional<RepositoryCheckBook> actualBook = repository.findById(id);
      assertTrue(actualBook.isPresent());
      assertThat(actualBook.get().getId()).isEqualTo(id);
      assertThat(actualBook.get().getTitle()).isEqualTo(TITLE);
   }

   private RepositoryCheckBook getBookById(long id) {
      Optional<RepositoryCheckBook> book = repository.findById(id);
      assertTrue(book.isPresent());
      return book.get();
   }
}