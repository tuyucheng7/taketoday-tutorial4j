package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.backend.KubernetesBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KubernetesBackendApplication.class)
class SpringContextTest {

   @Test
   void contextLoads() {
   }
}