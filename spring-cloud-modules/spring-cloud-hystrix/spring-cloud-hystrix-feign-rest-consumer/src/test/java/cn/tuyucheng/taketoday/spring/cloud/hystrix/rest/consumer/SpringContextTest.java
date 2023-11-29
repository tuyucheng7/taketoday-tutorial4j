package cn.tuyucheng.taketoday.spring.cloud.hystrix.rest.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@ContextConfiguration(classes = RestConsumerFeignApplication.class)
@WebAppConfiguration
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}