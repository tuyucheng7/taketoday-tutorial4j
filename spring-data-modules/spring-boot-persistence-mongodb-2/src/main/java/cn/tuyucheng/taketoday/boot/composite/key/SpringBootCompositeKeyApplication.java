package cn.tuyucheng.taketoday.boot.composite.key;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"cn.tuyucheng.taketoday.boot.composite.key"})
public class SpringBootCompositeKeyApplication {

	public static void main(String... args) {
		SpringApplication.run(SpringBootCompositeKeyApplication.class, args);
	}
}