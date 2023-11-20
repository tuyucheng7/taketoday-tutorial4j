package cn.tuyucheng.taketoday.nullablebean.nullablespring;

import cn.tuyucheng.taketoday.nullablebean.MainComponent;
import cn.tuyucheng.taketoday.nullablebean.SubComponent;

import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NullableSupplierConfiguration {

   @Bean
   public MainComponent mainComponent(Supplier<SubComponent> subComponentSupplier) {
      return new MainComponent(subComponentSupplier.get());
   }

   @Bean
   public Supplier<SubComponent> subComponentSupplier() {
      return () -> null;
   }
}