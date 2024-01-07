package cn.tuyucheng.taketoday;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tuyucheng.taketoday.webflux.EmployeeWebSocketClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeWebSocketClient.class)
public class SpringContextTest {

   @Test
   public void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}