package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.data.cassandra.config.CassandraConfig;
import cn.tuyucheng.taketoday.spring.data.cassandra.model.Book;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashMap;

@Disabled("fails test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CassandraConfig.class)
class SpringContextTest {

   public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace " + "WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

   public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

   public static final String DATA_TABLE_NAME = "book";

   @Autowired
   private CassandraAdminOperations adminTemplate;

   @BeforeAll
   static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
      EmbeddedCassandraServerHelper.startEmbeddedCassandra();
      final Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
      final Session session = cluster.connect();
      session.execute(KEYSPACE_CREATION_QUERY);
      session.execute(KEYSPACE_ACTIVATE_QUERY);
      Thread.sleep(5000);
   }

   @BeforeEach
   void createTable() {
      adminTemplate.createTable(true, CqlIdentifier.cqlId(DATA_TABLE_NAME), Book.class, new HashMap<>());
   }

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
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