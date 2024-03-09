package cn.tuyucheng.taketoday.skipselectbeforeinsert;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
      value = "cn.tuyucheng.taketoday.skipselectbeforeinsert.repository",
      repositoryBaseClass = BaseJpaRepositoryImpl.class
)
public class SkipSelectBeforeInsertApplication {
   public static void main(String[] args) {
      SpringApplication.run(SkipSelectBeforeInsertApplication.class, args);
   }
}