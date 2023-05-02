package cn.tuyucheng.taketoday.rsocketexchange;

import cn.tuyucheng.taketoday.rsocketexchange.declarativeClient.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.service.RSocketServiceProxyFactory;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class RSocketExchangeApp implements CommandLineRunner {

   public static void main(String[] args) {
      SpringApplication.run(RSocketExchangeApp.class, args);
   }

   @Autowired
   RSocketRequester.Builder requesterBuilder;

   @Override
   public void run(String... args) throws Exception {
      RSocketRequester rsocketRequester = requesterBuilder.tcp("localhost", 7000);
      RSocketServiceProxyFactory factory = RSocketServiceProxyFactory.builder(rsocketRequester).build();

      MessageService service = factory.createClient(MessageService.class);

      Mono<String> response = service.sendMessage("Tuyucheng", Mono.just("Hello there!"));
      response.subscribe(message -> LOGGER.info("RSocket response : {}", message));
   }
}