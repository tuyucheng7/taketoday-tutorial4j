package cn.tuyucheng.taketoday.spring.data.cassandra.repository;

import cn.tuyucheng.taketoday.spring.data.cassandra.config.CassandraConfig;
import cn.tuyucheng.taketoday.spring.data.cassandra.model.Book;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Live test for Cassandra testing.
 * This can be converted to IntegrationTest once cassandra-unit tests can be executed in parallel and
 * multiple test servers started as part of test suite.
 * Open cassandra-unit issue for parallel execution: https://github.com/jsevellec/cassandra-unit/issues/155
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CassandraConfig.class)
class CassandraTemplateLiveTest {
   private static final Log LOGGER = LogFactory.getLog(CassandraTemplateLiveTest.class);

   public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace " + "WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

   public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

   public static final String DATA_TABLE_NAME = "book";

   @Autowired
   private CassandraAdminOperations adminTemplate;

   @Autowired
   private CassandraOperations cassandraTemplate;

   @BeforeAll
   static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
      EmbeddedCassandraServerHelper.startEmbeddedCassandra();
      final Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
      LOGGER.info("Server Started at 127.0.0.1:9142... ");
      final Session session = cluster.connect();
      session.execute(KEYSPACE_CREATION_QUERY);
      session.execute(KEYSPACE_ACTIVATE_QUERY);
      LOGGER.info("KeySpace created and activated.");
      Thread.sleep(5000);
   }

   @BeforeEach
   void createTable() {
      adminTemplate.createTable(true, CqlIdentifier.cqlId(DATA_TABLE_NAME), Book.class, new HashMap<>());
   }

   @Test
   void whenSavingBook_thenAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      cassandraTemplate.insert(javaBook);
      final Select select = QueryBuilder.select().from("book").where(QueryBuilder.eq("title", "Head First Java")).and(QueryBuilder.eq("publisher", "O'Reilly Media")).limit(10);
      final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
      assertEquals(javaBook.getId(), retrievedBook.getId());
   }

   @Test
   void whenSavingBooks_thenAllAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      final List<Book> bookList = new ArrayList<>();
      bookList.add(javaBook);
      bookList.add(dPatternBook);
      cassandraTemplate.insert(bookList);

      final Select select = QueryBuilder.select().from("book").limit(10);
      final List<Book> retrievedBooks = cassandraTemplate.select(select, Book.class);
      assertThat(retrievedBooks.size(), is(2));
      assertEquals(javaBook.getId(), retrievedBooks.get(0).getId());
      assertEquals(dPatternBook.getId(), retrievedBooks.get(1).getId());
   }

   @Test
   void whenUpdatingBook_thenShouldUpdatedOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      cassandraTemplate.insert(javaBook);
      final Select select = QueryBuilder.select().from("book").limit(10);
      final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
      retrievedBook.setTags(ImmutableSet.of("Java", "Programming"));
      cassandraTemplate.update(retrievedBook);
      final Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
      assertEquals(retrievedBook.getTags(), retrievedUpdatedBook.getTags());
   }

   @Test
   void whenDeletingASelectedBook_thenNotAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "OReilly Media", ImmutableSet.of("Computer", "Software"));
      cassandraTemplate.insert(javaBook);
      cassandraTemplate.delete(javaBook);
      final Select select = QueryBuilder.select().from("book").limit(10);
      final Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
      assertNull(retrievedUpdatedBook);
   }

   @Test
   void whenDeletingAllBooks_thenNotAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      cassandraTemplate.insert(javaBook);
      cassandraTemplate.insert(dPatternBook);
      cassandraTemplate.deleteAll(Book.class);
      final Select select = QueryBuilder.select().from("book").limit(10);
      final Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
      assertNull(retrievedUpdatedBook);
   }

   @Test
   void whenAddingBooks_thenCountShouldBeCorrectOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      cassandraTemplate.insert(javaBook);
      cassandraTemplate.insert(dPatternBook);
      final long bookCount = cassandraTemplate.count(Book.class);
      assertEquals(2, bookCount);
   }

   @AfterEach
   void dropTable() {
      adminTemplate.dropTable(CqlIdentifier.cqlId(DATA_TABLE_NAME));
   }

   @AfterAll
   static void stopCassandraEmbedded() {
      EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
   }
}