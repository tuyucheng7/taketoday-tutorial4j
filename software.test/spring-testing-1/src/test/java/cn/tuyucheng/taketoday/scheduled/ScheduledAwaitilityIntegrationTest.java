package cn.tuyucheng.taketoday.scheduled;

import cn.tuyucheng.taketoday.config.ScheduledConfig;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(ScheduledConfig.class)
class ScheduledAwaitilityIntegrationTest {

   @SpyBean
   private Counter counter;

   @Test
   void whenWaitOneSecond_thenScheduledIsCalledAtLeastTenTimes() {
      await().atMost(Duration.ONE_SECOND)
            .untilAsserted(() -> verify(counter, atLeast(10)).scheduled());
   }
}