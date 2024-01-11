package cn.tuyucheng.taketoday.uuid;

import cn.tuyucheng.taketoday.uuid.config.EventMongoConfig;
import cn.tuyucheng.taketoday.uuid.model.Book;
import cn.tuyucheng.taketoday.uuid.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EventMongoConfig.class)
class EventLiveTest {

   @Autowired
   private BookRepository bookRepository;

   @Autowired
   private MongoOperations mongoOps;

   @BeforeEach
   void testSetup() {
      if (!mongoOps.collectionExists(Book.class)) {
         mongoOps.createCollection(Book.class);
      }
   }

   @AfterEach
   void tearDown() {
      mongoOps.dropCollection(Book.class);
   }

   @Test
   void whenSavingArticle_thenArticleIsInserted() {
      final Book book = new Book();
      book.setTitle("The Lord of the Rings");
      book.setAuthor("JRR Tolkien");

      Book savedArticle = bookRepository.save(book);

      Book result = mongoOps.findOne(Query.query(Criteria.where("_id").is(savedArticle.getId())), Book.class);

      assertNotNull(result);
      assertEquals(result.getTitle(), "The Lord of the Rings");
   }


}
