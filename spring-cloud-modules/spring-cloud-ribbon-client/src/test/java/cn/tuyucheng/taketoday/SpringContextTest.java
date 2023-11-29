package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.ribbon.client.ServerLocationApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ServerLocationApp.class)
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}