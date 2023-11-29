package cn.tuyucheng.taketoday.spring.cloud.aws.sns;

import cn.tuyucheng.taketoday.spring.cloud.aws.SpringCloudAwsTestUtil;
import cn.tuyucheng.taketoday.spring.cloud.aws.sqs.Greeting;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

/**
 * To run this Live Test, we need to have an AWS account and have API keys generated for programmatic access.
 * <p>
 * Check the README file in this module for more information.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
class SpringCloudSNSLiveTest {

   @Autowired
   private SNSMessageSender snsMessageSender;

   private static String topicName;
   private static String topicArn;

   @BeforeAll
   static void setupAwsResources() {
      topicName = UUID.randomUUID().toString();

      AmazonSNS amazonSNS = SpringCloudAwsTestUtil.amazonSNS();

      CreateTopicResult result = amazonSNS.createTopic(topicName);
      topicArn = result.getTopicArn();
   }

   @Test
   void whenMessagePublished_thenSuccess() {
      String subject = "Test Message";
      String message = "Hello World";
      snsMessageSender.send(topicName, message, subject);
   }

   @Test
   void whenConvertedMessagePublished_thenSuccess() {
      String subject = "Test Message";
      Greeting message = new Greeting("Hello", "World");
      snsMessageSender.send(topicName, message, subject);
   }

   @AfterAll
   static void cleanupAwsResources() {
      AmazonSNS amazonSNS = SpringCloudAwsTestUtil.amazonSNS();
      amazonSNS.deleteTopic(topicArn);
   }
}