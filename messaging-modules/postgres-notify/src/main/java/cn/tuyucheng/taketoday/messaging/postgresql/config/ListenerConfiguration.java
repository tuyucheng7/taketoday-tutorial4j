package cn.tuyucheng.taketoday.messaging.postgresql.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.tuyucheng.taketoday.messaging.postgresql.service.NotifierService;
import cn.tuyucheng.taketoday.messaging.postgresql.service.NotificationHandler;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ListenerConfiguration {

   @Bean
   CommandLineRunner startListener(NotifierService notifier, NotificationHandler handler) {
      return (args) -> {
         LOGGER.info("Starting order listener thread...");
         Runnable listener = notifier.createNotificationHandler(handler);
         Thread t = new Thread(listener, "order-listener");
         t.start();
      };
   }
}