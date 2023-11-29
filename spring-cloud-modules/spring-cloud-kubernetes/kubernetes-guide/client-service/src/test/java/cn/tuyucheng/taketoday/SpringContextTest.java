package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.client.Application;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class SpringContextTest {

   @Test
   void contextLoads() {
   }
}