package cn.tuyucheng.taketoday.overridebean.profile;

import cn.tuyucheng.taketoday.overridebean.Service;
import cn.tuyucheng.taketoday.overridebean.ServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProfileConfig {

   @Bean
   public Service helloWorld() {
      return new ServiceImpl();
   }
}