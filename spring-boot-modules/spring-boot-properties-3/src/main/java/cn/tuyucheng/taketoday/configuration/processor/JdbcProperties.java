package cn.tuyucheng.taketoday.configuration.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cn.tuyucheng.taketoday")
public class JdbcProperties {

	@Value("${jdbc.url:jdbc:postgresql:/localhost:5432}")
	private String jdbcUrl;

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
}