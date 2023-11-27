package cn.tuyucheng.taketoday.overridebean.conditional;

import cn.tuyucheng.taketoday.overridebean.Service;
import cn.tuyucheng.taketoday.overridebean.ServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConditionalConfig {

   @Bean
   @ConditionalOnProperty(name = "service.stub", havingValue = "false")
   public Service helloWorld() {
      return new ServiceImpl();
   }
}