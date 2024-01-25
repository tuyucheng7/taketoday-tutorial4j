package cn.tuyucheng.taketoday.springmodulith.events.externalization.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestListener {
   private List<String> events = new ArrayList<>();

   @KafkaListener(id = "test-id", topics = "tuyucheng.articles.published")
   public void listen(String event) {
      events.add(event);
   }

   public List<String> getEvents() {
      return events;
   }

   public void reset() {
      events = new ArrayList<>();
   }
}