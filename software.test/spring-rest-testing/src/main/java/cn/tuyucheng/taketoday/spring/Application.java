package cn.tuyucheng.taketoday.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Application Class - uses Spring Boot. Just run this as a normal Java
 * class to run up a Jetty Server (on http://localhost:8082/spring-rest-full)
 */
@EnableAutoConfiguration
@ComponentScan("cn.tuyucheng.taketoday")
@SpringBootApplication
public class Application {

   public static void main(final String[] args) {
      SpringApplication.run(Application.class, args);
   }
}