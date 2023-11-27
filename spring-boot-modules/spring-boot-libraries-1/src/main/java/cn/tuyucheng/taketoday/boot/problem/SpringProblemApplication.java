package cn.tuyucheng.taketoday.boot.problem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringProblemApplication {

   public static void main(String[] args) {
      System.setProperty("spring.profiles.active", "problem");
      SpringApplication.run(SpringProblemApplication.class, args);
   }
}