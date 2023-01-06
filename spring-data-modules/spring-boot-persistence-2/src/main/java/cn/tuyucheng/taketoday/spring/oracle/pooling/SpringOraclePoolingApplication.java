package cn.tuyucheng.taketoday.spring.oracle.pooling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
@Slf4j
public class SpringOraclePoolingApplication implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(SpringOraclePoolingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Connection Polling datasource : " + dataSource);

	}
}