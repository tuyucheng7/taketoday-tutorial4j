package cn.tuyucheng.taketoday.overridebean.profile;

import cn.tuyucheng.taketoday.overridebean.Service;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ProfileTestConfig {

   @Bean
   @Profile("stub")
   public Service helloWorldStub() {
      return new ProfileServiceStub();
   }

   @Bean
   @Profile("mock")
   public Service helloWorldMock() {
      return mock(Service.class);
   }
}