package cn.tuyucheng.taketoday.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("cn.tuyucheng.taketoday.jpa")
@EnableJpaRepositories("cn.tuyucheng.taketoday.jpa.repository")
public class JpaApplication {

   public static void main(String[] args) {
      SpringApplication.run(JpaApplication.class, args);
   }
}