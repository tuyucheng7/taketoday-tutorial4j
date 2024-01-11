package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.reactive.Spring5ReactiveApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Spring5ReactiveApplication.class)
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}
