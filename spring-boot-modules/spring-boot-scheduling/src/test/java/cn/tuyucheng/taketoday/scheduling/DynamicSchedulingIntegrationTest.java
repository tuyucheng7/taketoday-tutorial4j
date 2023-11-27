package cn.tuyucheng.taketoday.scheduling;

import cn.tuyucheng.taketoday.scheduling.dynamic.DynamicSchedulingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
      DynamicSchedulingConfig.class}, loader = AnnotationConfigContextLoader.class)
class DynamicSchedulingIntegrationTest {

   @Test
   void testTickServiceTick() throws InterruptedException {
      Thread.sleep(6000);
   }
}