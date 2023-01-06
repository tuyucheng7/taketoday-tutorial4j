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
@ActiveProfiles({"oracle-pooling-basic", "oracle"})
public class SpringOraclePoolingApplicationOracleLiveTest {

	@Autowired
	private DataSource dataSource;

	@Test
	public void givenOracleConfiguration_thenBuildsOracleDataSource() {
		assertTrue(dataSource instanceof oracle.jdbc.pool.OracleDataSource);
	}
}