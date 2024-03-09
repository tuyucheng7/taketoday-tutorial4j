package cn.tuyucheng.taketoday.springmultipledatasources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class MultipleDataSourceApplication {

   public static void main(String[] args) {
      System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "multipledatasources");
      SpringApplication.run(MultipleDataSourceApplication.class, args);
   }
}