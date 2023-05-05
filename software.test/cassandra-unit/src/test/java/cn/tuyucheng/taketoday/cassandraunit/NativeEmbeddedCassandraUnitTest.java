package cn.tuyucheng.taketoday.cassandraunit;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NativeEmbeddedCassandraUnitTest {

   private CqlSession session;

   @Before
   public void setUp() throws Exception {
      EmbeddedCassandraServerHelper.startEmbeddedCassandra();
      session = EmbeddedCassandraServerHelper.getSession();
      new CQLDataLoader(session).load(new ClassPathCQLDataSet("people.cql", "people"));
   }

   @After
   public void tearDown() throws Exception {
      EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
   }

   @Test
   public void givenEmbeddedCassandraInstance_whenStarted_thenQuerySuccess() throws Exception {
      ResultSet result = session.execute("select * from person WHERE id=1234");
      assertThat(result.iterator().next().getString("name"), is("Eugen"));
   }
}