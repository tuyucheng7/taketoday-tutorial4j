package cn.tuyucheng.taketoday.spring.cloud.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import cn.tuyucheng.taketoday.spring.cloud.aws.sqs.EventQueuesProperties;

@SpringBootApplication
@EnableConfigurationProperties(EventQueuesProperties.class)
public class SpringCloudAwsApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringCloudAwsApplication.class, args);
   }
}