package cn.tuyucheng.taketoday.failureanalyzer;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Profile("failureanalyzer")
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.failureanalyzer")
public class FailureAnalyzerApplication {

   @RolesAllowed("*")
   public static void main(String[] args) {
      SpringApplication.run(FailureAnalyzerApplication.class, args);
   }
}