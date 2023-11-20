package cn.tuyucheng.taketoday.nullablebean.optionable;

import cn.tuyucheng.taketoday.nullablebean.MainComponent;
import cn.tuyucheng.taketoday.nullablebean.SubComponent;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OptionableConfiguration {

   @Bean
   public MainComponent mainComponent(Optional<SubComponent> optionalSubComponent) {
      return new MainComponent(optionalSubComponent.orElse(null));
   }
}