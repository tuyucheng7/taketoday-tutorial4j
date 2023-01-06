package cn.tuyucheng.taketoday.dsrouting;

import cn.tuyucheng.taketoday.dsrouting.model.ClientADetails;
import cn.tuyucheng.taketoday.dsrouting.model.ClientBDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SpringBootDataSourceRoutingTestConfiguration {
	@Autowired
	private ClientADetails clientADetails;
	@Autowired
	private ClientBDetails clientBDetails;

	@Bean
	public ClientService clientService() {
		return new ClientService(new ClientDao(clientDatasource()));
	}

	@Bean
	public DataSource clientDatasource() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		DataSource clientADatasource = clientADatasource();
		DataSource clientBDatasource = clientBDatasource();
		targetDataSources.put(ClientDatabase.CLIENT_A, clientADatasource);
		targetDataSources.put(ClientDatabase.CLIENT_B, clientBDatasource);

		ClientDataSourceRouter clientRoutingDatasource = new ClientDataSourceRouter();
		clientRoutingDatasource.setTargetDataSources(targetDataSources);
		clientRoutingDatasource.setDefaultTargetDataSource(clientADatasource);
		return clientRoutingDatasource;
	}

	private DataSource clientADatasource() {
		EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
		return dbBuilder.setType(EmbeddedDatabaseType.H2)
			.setName(clientADetails.getName())
			.addScript(clientADetails.getScript())
			.build();
	}

	private DataSource clientBDatasource() {
		EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
		return dbBuilder.setType(EmbeddedDatabaseType.H2)
			.setName(clientBDetails.getName())
			.addScript(clientBDetails.getScript())
			.build();
	}
}