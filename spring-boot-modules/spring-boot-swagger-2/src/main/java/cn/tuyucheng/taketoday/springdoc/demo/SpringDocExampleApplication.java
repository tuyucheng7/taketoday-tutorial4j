package cn.tuyucheng.taketoday.springdoc.demo;

import cn.tuyucheng.taketoday.springdoc.demo.config.SpringDocSwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDocExampleApplication {

   public static void main(String[] args) {
      SpringApplication application = new SpringApplication(SpringDocExampleApplication.class);
      // Note: SpringDocExampleApplication is the name of your main class
      application.addListeners(new SpringDocSwaggerConfig());
      application.run(args);
   }
}