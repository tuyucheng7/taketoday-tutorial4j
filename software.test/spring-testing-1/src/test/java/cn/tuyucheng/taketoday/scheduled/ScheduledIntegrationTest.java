package cn.tuyucheng.taketoday.scheduled;

import cn.tuyucheng.taketoday.config.ScheduledConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ScheduledConfig.class)
class ScheduledIntegrationTest {

   @Autowired
   Counter counter;

   @Test
   void givenSleepBy100ms_whenGetInvocationCount_thenIsGreaterThanZero() throws InterruptedException {
      Thread.sleep(100L);
      assertThat(counter.getInvocationCount()).isGreaterThan(0);
   }
}