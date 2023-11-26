package cn.tuyucheng.taketoday.overridebean.primary;

import cn.tuyucheng.taketoday.overridebean.Service;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class PrimaryTestConfig {

   @Primary
   @Bean("service.stub")
   public Service helloWorld() {
      return new PrimaryServiceStub();
   }
}