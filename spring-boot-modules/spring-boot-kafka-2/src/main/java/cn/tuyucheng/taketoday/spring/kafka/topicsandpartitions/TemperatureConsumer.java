package cn.tuyucheng.taketoday.spring.kafka.topicsandpartitions;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Service
public class TemperatureConsumer {

   private CountDownLatch latch = new CountDownLatch(1);

   Map<String, Set<String>> consumedRecords = new ConcurrentHashMap<>();

   @KafkaListener(topics = "celcius-scale-topic", groupId = "group-1")
   public void consumer1(ConsumerRecord<?, ?> consumerRecord) {
      trackConsumedPartitions("consumer-1", consumerRecord.partition());
   }

   private void trackConsumedPartitions(String consumerName, int partitionNumber) {
      consumedRecords.computeIfAbsent(consumerName, k -> new HashSet<>());
      consumedRecords.computeIfPresent(consumerName, (k, v) -> {
         v.add(String.valueOf(partitionNumber));
         return v;
      });
   }

   public CountDownLatch getLatch() {
      return latch;
   }
}