package cn.tuyucheng.taketoday.buildproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:build.properties")
//@PropertySource("classpath:build.yml")
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}