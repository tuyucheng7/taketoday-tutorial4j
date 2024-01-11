package cn.tuyucheng.taketoday.spring.data.cassandra.repository;

import cn.tuyucheng.taketoday.spring.data.cassandra.config.CassandraConfig;
import cn.tuyucheng.taketoday.spring.data.cassandra.model.Book;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Live test for Cassandra testing.
 * This can be converted to IntegrationTest once cassandra-unit tests can be executed in parallel and
 * multiple test servers started as part of test suite.
 * Open cassandra-unit issue for parallel execution: https://github.com/jsevellec/cassandra-unit/issues/155
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CassandraConfig.class)
class CqlQueriesLiveTest {
   private static final Log LOGGER = LogFactory.getLog(CqlQueriesLiveTest.class);

   public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace " + "WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

   public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

   public static final String DATA_TABLE_NAME = "book";

   @Autowired
   private CassandraAdminOperations adminTemplate;

   @Autowired
   private CassandraOperations cassandraTemplate;

   @BeforeAll
   static void startCassandraEmbedded() throws Exception {
      EmbeddedCassandraServerHelper.startEmbeddedCassandra(25000);
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
   void whenSavingBook_thenAvailableOnRetrieval_usingQueryBuilder() {
      final UUID uuid = UUIDs.timeBased();
      final Insert insert = QueryBuilder.insertInto(DATA_TABLE_NAME).value("id", uuid).value("title", "Head First Java").value("publisher", "OReilly Media").value("tags", ImmutableSet.of("Software"));
      cassandraTemplate.execute(insert);
      final Select select = QueryBuilder.select().from("book").limit(10);
      final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
      assertEquals(uuid, retrievedBook.getId());
   }

   @Test
   void whenSavingBook_thenAvailableOnRetrieval_usingCQLStatements() {
      final UUID uuid = UUIDs.timeBased();
      final String insertCql = "insert into book (id, title, publisher, tags) values " + "(" + uuid + ", 'Head First Java', 'OReilly Media', {'Software'})";
      cassandraTemplate.execute(insertCql);
      final Select select = QueryBuilder.select().from("book").limit(10);
      final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
      assertEquals(uuid, retrievedBook.getId());
   }

   @Test
   void whenSavingBook_thenAvailableOnRetrieval_usingPreparedStatements() throws InterruptedException {
      final UUID uuid = UUIDs.timeBased();
      final String insertPreparedCql = "insert into book (id, title, publisher, tags) values (?, ?, ?, ?)";
      final List<Object> singleBookArgsList = new ArrayList<>();
      final List<List<?>> bookList = new ArrayList<>();
      singleBookArgsList.add(uuid);
      singleBookArgsList.add("Head First Java");
      singleBookArgsList.add("OReilly Media");
      singleBookArgsList.add(ImmutableSet.of("Software"));
      bookList.add(singleBookArgsList);
      cassandraTemplate.ingest(insertPreparedCql, bookList);
      // This may not be required, just added to avoid any transient issues
      Thread.sleep(5000);
      final Select select = QueryBuilder.select().from("book");
      final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
      assertEquals(uuid, retrievedBook.getId());
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