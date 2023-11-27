package cn.tuyucheng.taketoday.scheduling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringSchedulingConfig.class}, loader = AnnotationConfigContextLoader.class)
class ScheduledFixedRateExampleIntegrationTest {

   @Test
   void testScheduledFixedRateAnnotation() throws InterruptedException {
      Thread.sleep(5000);
   }
}