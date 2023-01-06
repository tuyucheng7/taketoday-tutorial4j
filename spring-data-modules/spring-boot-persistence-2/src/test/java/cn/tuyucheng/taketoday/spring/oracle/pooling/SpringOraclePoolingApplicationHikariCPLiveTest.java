package cn.tuyucheng.taketoday.spring.oracle.pooling;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringOraclePoolingApplication.class})
@ActiveProfiles("oracle-pooling-basic")
public class SpringOraclePoolingApplicationHikariCPLiveTest {

	@Autowired
	private DataSource dataSource;

	@Test
	public void givenHikariCPConfiguration_thenBuildsHikariCP() {
		assertTrue(dataSource instanceof com.zaxxer.hikari.HikariDataSource);
	}
}