package cn.tuyucheng.taketoday.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class TestContainersApplication {

   public static void main(String[] args) {
      SpringApplication.run(TestContainersApplication.class, args);
   }
}