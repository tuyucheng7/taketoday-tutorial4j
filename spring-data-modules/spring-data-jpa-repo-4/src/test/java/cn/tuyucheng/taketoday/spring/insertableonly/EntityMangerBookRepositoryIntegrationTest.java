package cn.tuyucheng.taketoday.spring.insertableonly;

import cn.tuyucheng.taketoday.spring.insertableonly.entitymanager.EntityManagerBook;
import cn.tuyucheng.taketoday.spring.insertableonly.entitymanager.EntityManagerBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Optional;

import static cn.tuyucheng.taketoday.spring.insertableonly.ConstantHolder.NEW_TITLE;
import static cn.tuyucheng.taketoday.spring.insertableonly.ConstantHolder.TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
class EntityMangerBookRepositoryIntegrationTest {

   @Autowired
   private EntityManagerBookRepository repository;

   @BeforeEach
   void setup() {
      repository.deleteAll();
   }

   @Test
   void givenDatasourceWhenSaveBookThenBookPersisted() {
      EntityManagerBook newBook = new EntityManagerBook(TITLE);
      EntityManagerBook persistedBook = repository.persist(newBook);
      assertThat(persistedBook.getId()).isNotNull();
      Long id = persistedBook.getId();
      EntityManagerBook actualBook = getBookById(id);
      assertThat(actualBook.getId()).isEqualTo(id);
      assertThat(actualBook.getTitle()).isEqualTo(TITLE);
   }

   @Test
   void givenDatasourceWhenUpdateThenThrowException() {
      EntityManagerBook book = new EntityManagerBook(TITLE);
      EntityManagerBook persistedBook = repository.persist(book);
      persistedBook.setTitle(NEW_TITLE);
      assertThatExceptionOfType(InvalidDataAccessApiUsageException.class)
            .isThrownBy(() -> repository.persist(persistedBook));
   }

   private EntityManagerBook getBookById(long id) {
      Optional<EntityManagerBook> book = repository.findById(id);
      assertTrue(book.isPresent());
      return book.get();
   }
}