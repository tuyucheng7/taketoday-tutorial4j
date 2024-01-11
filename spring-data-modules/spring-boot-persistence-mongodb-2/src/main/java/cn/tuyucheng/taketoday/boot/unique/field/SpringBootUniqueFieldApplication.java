package cn.tuyucheng.taketoday.boot.unique.field;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@PropertySource("classpath:boot.unique.field/app.properties")
@EnableMongoRepositories(basePackages = {"cn.tuyucheng.taketoday.boot.unique.field"})
public class SpringBootUniqueFieldApplication {

   public static void main(String... args) {
      SpringApplication.run(SpringBootUniqueFieldApplication.class, args);
   }
}