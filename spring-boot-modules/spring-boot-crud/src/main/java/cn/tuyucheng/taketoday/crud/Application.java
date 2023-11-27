package cn.tuyucheng.taketoday.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "cn.tuyucheng.taketoday.crud.repositories")
@EnableTransactionManagement
@EntityScan(basePackages = "cn.tuyucheng.taketoday.crud.entities")
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}