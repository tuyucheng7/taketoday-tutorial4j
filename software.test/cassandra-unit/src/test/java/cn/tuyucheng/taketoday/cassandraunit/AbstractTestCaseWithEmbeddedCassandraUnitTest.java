package cn.tuyucheng.taketoday.cassandraunit;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.cassandraunit.AbstractCassandraUnit4CQLTestCase;
import org.cassandraunit.dataset.CQLDataSet;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AbstractTestCaseWithEmbeddedCassandraUnitTest extends AbstractCassandraUnit4CQLTestCase {

   @Override
   public CQLDataSet getDataSet() {
      return new ClassPathCQLDataSet("people.cql", "people");
   }

   @Test
   public void givenEmbeddedCassandraInstance_whenStarted_thenQuerySuccess() throws Exception {
      ResultSet result = this.getSession().execute("select * from person WHERE id=1234");
      assertThat(result.iterator().next().getString("name"), is("Eugen"));
   }
}