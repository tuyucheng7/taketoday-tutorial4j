package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.TimeSourceApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TimeSourceApplication.class)
class SpringContextTest {

   @Test
   void contextLoads() {
   }
}