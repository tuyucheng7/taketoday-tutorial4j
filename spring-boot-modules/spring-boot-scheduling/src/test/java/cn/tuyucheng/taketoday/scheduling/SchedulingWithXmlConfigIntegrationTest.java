package cn.tuyucheng.taketoday.scheduling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:springScheduled-config.xml")
class SchedulingWithXmlConfigIntegrationTest {

   @Test
   void testXmlBasedScheduling() throws InterruptedException {
      Thread.sleep(5000);
   }
}