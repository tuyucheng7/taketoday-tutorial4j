package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.task.JobConfiguration;
import cn.tuyucheng.taketoday.task.TaskDemo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
@ContextConfiguration(classes = {JobConfiguration.class, TaskDemo.class}, initializers = {ConfigDataApplicationContextInitializer.class})
class SpringContextTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}