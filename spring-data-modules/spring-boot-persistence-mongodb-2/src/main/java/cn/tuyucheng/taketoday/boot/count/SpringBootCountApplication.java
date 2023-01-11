package cn.tuyucheng.taketoday.boot.count;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"cn.tuyucheng.taketoday.boot.count"})
public class SpringBootCountApplication {

    public static void main(String... args) {
        SpringApplication.run(SpringBootCountApplication.class, args);
    }
}