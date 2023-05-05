package cn.tuyucheng.taketoday.cassandraunit;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({CassandraUnitTestExecutionListener.class})
@CassandraDataSet(value = "people.cql", keyspace = "people")
@EmbeddedCassandra
public class SpringWithEmbeddedCassandraUnitTest {

   @Test
   public void givenEmbeddedCassandraInstance_whenStarted_thenQuerySuccess() throws Exception {
      CqlSession session = EmbeddedCassandraServerHelper.getSession();

      ResultSet result = session.execute("select * from person WHERE id=1234");
      assertThat(result.iterator().next().getString("name"), is("Eugen"));
   }
}