package cn.tuyucheng.taketoday.dsrouting;

import cn.tuyucheng.taketoday.dsrouting.model.ClientADetails;
import cn.tuyucheng.taketoday.dsrouting.model.ClientBDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = {ClientADetails.class, ClientBDetails.class})
@ContextConfiguration(classes = SpringBootDataSourceRoutingTestConfiguration.class)
@DirtiesContext
@EnableConfigurationProperties(ClientBDetails.class)
class SpringBootDataSourceRoutingIntegrationTest {

	@Autowired
	DataSource routingDatasource;

	@Autowired
	ClientService clientService;

	@BeforeEach
	void setup() {
		final String SQL_CLIENT_A = "insert into client (id, name) values (1, 'CLIENT A')";
		final String SQL_CLIENT_B = "insert into client (id, name) values (2, 'CLIENT B')";

		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(routingDatasource);

		ClientDatabaseContextHolder.set(ClientDatabase.CLIENT_A);
		jdbcTemplate.execute(SQL_CLIENT_A);
		ClientDatabaseContextHolder.clear();

		ClientDatabaseContextHolder.set(ClientDatabase.CLIENT_B);
		jdbcTemplate.execute(SQL_CLIENT_B);
		ClientDatabaseContextHolder.clear();
	}

	@Test
	void givenClientDbs_whenContextsSwitch_thenRouteToCorrectDatabase() throws Exception {
		// test ACME WIDGETS
		String clientName = clientService.getClientName(ClientDatabase.CLIENT_A);
		assertEquals(clientName, "CLIENT A");

		// test WIDGETS_ARE_US
		clientName = clientService.getClientName(ClientDatabase.CLIENT_B);
		assertEquals(clientName, "CLIENT B");
	}
}