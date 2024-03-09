package cn.tuyucheng.taketoday.spring.cloud.aws.sqs.introduction;

import cn.tuyucheng.taketoday.spring.cloud.aws.sqs.acknowledgement.OrderProcessingApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EventQueuesProperties.class)
public class UserServiceApplication {

   public static void main(String[] args) {
      SpringApplication app = new SpringApplication(OrderProcessingApplication.class);
      app.setAdditionalProfiles("introduction");
      app.run(args);
   }
}