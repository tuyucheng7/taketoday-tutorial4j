package cn.tuyucheng.taketoday.restvalidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.restvalidation"})
public class RestValidationApplication {

   public static void main(String[] args) {
      SpringApplication.run(RestValidationApplication.class, args);
   }
}