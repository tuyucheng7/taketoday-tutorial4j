package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.DataFlowServerApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DataFlowServerApplication.class)
class SpringContextTest {

   @Test
   void contextLoads() {
   }
}