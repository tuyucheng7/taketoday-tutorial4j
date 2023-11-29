package cn.tuyucheng.taketoday.spring.cloud.aws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * To run this Live Test, we need to have an AWS account and have API keys generated for programmatic access.
 * <p>
 * Check the README file in this module for more information.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringCloudAwsApplication.class)
class SpringContextLiveTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}