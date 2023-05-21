package cn.tuyucheng.taketoday.dubbo.demo.test;

import cn.tuyucheng.taketoday.dubbo.demo.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ConsumerLiveTest {
   @DubboReference
   private DemoService demoService;

   @Test
   void test() {
      String result = demoService.sayHello("world");

      assertEquals("Hello world", result);
   }
}