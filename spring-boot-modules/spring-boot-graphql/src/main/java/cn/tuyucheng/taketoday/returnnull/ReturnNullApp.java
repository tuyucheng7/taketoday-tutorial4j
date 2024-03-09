package cn.tuyucheng.taketoday.returnnull;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReturnNullApp {

   public static void main(String[] args) {
      System.setProperty("spring.profiles.default", "returnnull");
      SpringApplication.run(cn.tuyucheng.taketoday.chooseapi.ChooseApiApp.class, args);
   }
}