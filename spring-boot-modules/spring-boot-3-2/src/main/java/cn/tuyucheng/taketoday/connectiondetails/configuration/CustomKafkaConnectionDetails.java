package cn.tuyucheng.taketoday.connectiondetails.configuration;

import cn.tuyucheng.taketoday.connectiondetails.adapter.VaultAdapter;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;

import java.util.List;

public class CustomKafkaConnectionDetails implements KafkaConnectionDetails {

   @Override
   public List<String> getBootstrapServers() {
      return List.of(VaultAdapter.getSecret("kafka_servers"));
   }
}