package cn.tuyucheng.taketoday.kong;

import org.jobrunr.autoconfigure.JobRunrAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {JobRunrAutoConfiguration.class})
public class StockApplication {

   public static void main(String[] args) {
      SpringApplication.run(StockApplication.class, args);
   }
}