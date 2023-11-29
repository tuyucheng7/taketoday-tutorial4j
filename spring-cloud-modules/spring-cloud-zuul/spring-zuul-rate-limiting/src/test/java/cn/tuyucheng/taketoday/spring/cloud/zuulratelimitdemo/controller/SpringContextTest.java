package cn.tuyucheng.taketoday.spring.cloud.zuulratelimitdemo.controller;

import cn.tuyucheng.taketoday.spring.cloud.zuulratelimitdemo.ZuulRatelimitDemoApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ZuulRatelimitDemoApplication.class)
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}