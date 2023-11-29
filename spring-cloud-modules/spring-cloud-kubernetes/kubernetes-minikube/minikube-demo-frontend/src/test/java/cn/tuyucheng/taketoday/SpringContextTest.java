package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.frontend.KubernetesFrontendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KubernetesFrontendApplication.class)
class SpringContextTest {

   @Test
   void contextLoads() {
   }
}