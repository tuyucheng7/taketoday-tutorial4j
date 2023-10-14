package cn.tuyucheng.taketoday.springcloudgateway.introduction;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tuyucheng.taketoday.springcloudgateway.introduction.IntroductionGatewayApplication;

@SpringBootTest(classes = IntroductionGatewayApplication.class)
public class SpringContextTest {

   @Test
   public void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}