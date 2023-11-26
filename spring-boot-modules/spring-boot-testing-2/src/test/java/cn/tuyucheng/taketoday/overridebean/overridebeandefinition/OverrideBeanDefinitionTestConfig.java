package cn.tuyucheng.taketoday.overridebean.overridebeandefinition;

import cn.tuyucheng.taketoday.overridebean.Service;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class OverrideBeanDefinitionTestConfig {

   @Bean
   public Service helloWorld() {
      return new OverrideBeanDefinitionServiceStub();
   }
}