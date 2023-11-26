package cn.tuyucheng.taketoday.overridebean.conditional;

import cn.tuyucheng.taketoday.overridebean.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ConditionalTestConfig {

   @Bean
   @ConditionalOnProperty(name = "service.stub", havingValue = "true")
   public Service helloWorld() {
      return new ConditionalStub();
   }
}