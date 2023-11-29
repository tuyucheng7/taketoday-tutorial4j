package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.greeting.GreetingApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GreetingApplication.class)
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}