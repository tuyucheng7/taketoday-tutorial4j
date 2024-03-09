package cn.tuyucheng.taketoday.graphql.intro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {
      SecurityAutoConfiguration.class
})
public class GraphqlApplication {

   public static void main(String[] args) {
      SpringApplication.run(GraphqlApplication.class, args);
   }
}