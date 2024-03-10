package cn.tuyucheng.taketoday.tutorials.openapi.quotes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfiguration {

   @Bean
   @ConditionalOnMissingBean
   Clock defaultClock() {
      return Clock.systemDefaultZone();
   }
}