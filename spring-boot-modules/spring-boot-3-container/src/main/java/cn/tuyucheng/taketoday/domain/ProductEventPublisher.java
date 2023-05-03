package cn.tuyucheng.taketoday.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {
   private final KafkaTemplate<String, Object> kafkaTemplate;

   public void publishProductCreatedEvent(Product product) {
      kafkaTemplate.send("products", product);
      LOGGER.info("ProductCreatedEvent sent to products topic");
   }
}