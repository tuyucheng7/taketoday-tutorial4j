package cn.tuyucheng.taketoday.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
/* the configuration requires the AS to be running */
public class SpringContextLiveTest {

   @Test
   public void whenLoadApplication_thenSuccess() {

   }

}