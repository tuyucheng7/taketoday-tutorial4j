package cn.tuyucheng.taketoday.tutorials.openapi.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class QuotesApplication {

   public static void main(String[] args) {
      SpringApplication.run(QuotesApplication.class, args);
   }
}