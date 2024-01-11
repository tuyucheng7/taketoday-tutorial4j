package cn.tuyucheng.taketoday.spring.data.cassandra.repository;

import cn.tuyucheng.taketoday.spring.data.cassandra.config.CassandraConfig;
import cn.tuyucheng.taketoday.spring.data.cassandra.model.Book;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Live test for Cassandra testing.
 * This can be converted to IntegrationTest once cassandra-unit tests can be executed in parallel and
 * multiple test servers started as part of test suite.
 * Open cassandra-unit issue for parallel execution: https://github.com/jsevellec/cassandra-unit/issues/155
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CassandraConfig.class)
class BookRepositoryLiveTest {
   private static final Log LOGGER = LogFactory.getLog(BookRepositoryLiveTest.class);

   public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

   public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

   public static final String DATA_TABLE_NAME = "book";

   @Autowired
   private BookRepository bookRepository;

   @Autowired
   private CassandraAdminOperations adminTemplate;

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
      bookRepository.save(ImmutableSet.of(javaBook));
      final Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
      assertEquals(javaBook.getId(), books.iterator().next().getId());
   }

   @Test
   void whenUpdatingBooks_thenAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      bookRepository.save(ImmutableSet.of(javaBook));
      final Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
      javaBook.setTitle("Head First Java Second Edition");
      bookRepository.save(ImmutableSet.of(javaBook));
      final Iterable<Book> updateBooks = bookRepository.findByTitleAndPublisher("Head First Java Second Edition", "O'Reilly Media");
      assertEquals(javaBook.getTitle(), updateBooks.iterator().next().getTitle());
   }

   // @Test(expected = java.util.NoSuchElementException.class)
   void whenDeletingExistingBooks_thenNotAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      bookRepository.save(ImmutableSet.of(javaBook));
      bookRepository.delete(javaBook);
      final Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
      assertNotEquals(javaBook.getId(), books.iterator().next().getId());
   }

   @Test
   void whenSavingBooks_thenAllShouldAvailableOnRetrieval() {
      final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
      bookRepository.save(ImmutableSet.of(javaBook));
      bookRepository.save(ImmutableSet.of(dPatternBook));
      final Iterable<Book> books = bookRepository.findAll();
      int bookCount = 0;
      for (final Book book : books) {
         bookCount++;
      }
      assertEquals(bookCount, 2);
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