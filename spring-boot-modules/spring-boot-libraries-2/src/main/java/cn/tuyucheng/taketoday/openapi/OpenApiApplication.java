package cn.tuyucheng.taketoday.openapi;

import org.jobrunr.autoconfigure.JobRunrAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {JobRunrAutoConfiguration.class})
public class OpenApiApplication {

   public static void main(String[] args) {
      SpringApplication.run(OpenApiApplication.class, args);
   }
}