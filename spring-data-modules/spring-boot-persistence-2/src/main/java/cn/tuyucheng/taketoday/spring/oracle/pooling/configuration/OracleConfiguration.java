package cn.tuyucheng.taketoday.spring.oracle.pooling.configuration;

import oracle.jdbc.pool.OracleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Profile("oracle")
public class OracleConfiguration {

	@Bean
	public DataSource dataSource() throws SQLException {
		OracleDataSource dataSource = new OracleDataSource();
		dataSource.setUser("books");
		dataSource.setPassword("books");
		dataSource.setURL("jdbc:oracle:thin:@//localhost:11521/ORCLPDB1");
		// Only with clients prior to v21
		// dataSource.setFastConnectionFailoverEnabled(true);
		dataSource.setImplicitCachingEnabled(true);
		// Only with clients prior to v11.2
		// dataSource.setConnectionCachingEnabled(true);
		return dataSource;
	}
}