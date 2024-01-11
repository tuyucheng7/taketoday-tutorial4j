package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }

   @Configuration
   public static class ContextConfiguration {
   }
}