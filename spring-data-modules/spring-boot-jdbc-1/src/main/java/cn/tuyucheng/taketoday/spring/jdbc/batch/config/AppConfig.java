package cn.tuyucheng.taketoday.spring.jdbc.batch.config;

import cn.tuyucheng.taketoday.spring.jdbc.batch.repo.BatchProductRepository;
import cn.tuyucheng.taketoday.spring.jdbc.batch.repo.SimpleProductRepository;
import cn.tuyucheng.taketoday.spring.jdbc.batch.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;
import java.util.Random;

@Configuration
@PropertySource("classpath:cn/tuyucheng/taketoday/spring/jdbc/batch/application.properties")
public class AppConfig {

   @Bean
   public ProductService simpleProductService(SimpleProductRepository simpleProductRepository) {
      return new ProductService(simpleProductRepository, new Random(), Clock.systemUTC());
   }

   @Bean
   public ProductService batchProductService(BatchProductRepository batchProductRepository) {
      return new ProductService(batchProductRepository, new Random(), Clock.systemUTC());
   }
}