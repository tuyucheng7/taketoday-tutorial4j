package cn.tuyucheng.taketoday.nullablebean.nullablespring;

import cn.tuyucheng.taketoday.nullablebean.MainComponent;
import cn.tuyucheng.taketoday.nullablebean.SubComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NullableConfiguration {

   @Bean
   public MainComponent mainComponent(SubComponent subComponent) {
      return new MainComponent(subComponent);
   }

   @Bean
   public SubComponent subComponent() {
      return null;
   }
}