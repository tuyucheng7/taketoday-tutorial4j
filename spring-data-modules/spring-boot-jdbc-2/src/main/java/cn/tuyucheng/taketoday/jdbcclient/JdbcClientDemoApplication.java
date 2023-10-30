package cn.tuyucheng.taketoday.jdbcclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.jdbcclient")
public class JdbcClientDemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(JdbcClientDemoApplication.class, args);
   }
}