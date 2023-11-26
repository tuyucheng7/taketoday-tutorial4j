package cn.tuyucheng.taketoday.changeport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

@Profile("customapplication")
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.changeport")
public class CustomApplication {

   public static void main(String[] args) {
      SpringApplication app = new SpringApplication(CustomApplication.class);
      app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
      app.run(args);
   }
}