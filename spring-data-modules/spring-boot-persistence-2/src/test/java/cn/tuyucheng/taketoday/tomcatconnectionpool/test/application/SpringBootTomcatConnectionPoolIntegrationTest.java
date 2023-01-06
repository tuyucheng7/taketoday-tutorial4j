package cn.tuyucheng.taketoday.tomcatconnectionpool.test.application;

import cn.tuyucheng.taketoday.tomcatconnectionpool.application.SpringBootConsoleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootConsoleApplication.class})
@TestPropertySource(properties = "spring.datasource.type=org.apache.tomcat.jdbc.pool.DataSource")
public class SpringBootTomcatConnectionPoolIntegrationTest {

	@Autowired
	private DataSource dataSource;

	@Test
	public void givenTomcatConnectionPoolInstance_whenCheckedPoolClassName_thenCorrect() {
		assertThat(dataSource.getClass().getName()).isEqualTo("org.apache.tomcat.jdbc.pool.DataSource");
	}
}