package cn.tuyucheng.taketoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday")
public class SpringBootPersistenceApplication {

	public static void main(String... args) {
		SpringApplication.run(SpringBootPersistenceApplication.class, args);
	}
}