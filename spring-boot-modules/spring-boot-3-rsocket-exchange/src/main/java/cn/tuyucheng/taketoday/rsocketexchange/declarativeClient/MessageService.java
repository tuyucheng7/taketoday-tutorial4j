package cn.tuyucheng.taketoday.rsocketexchange.declarativeClient;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.service.RSocketExchange;
import reactor.core.publisher.Mono;

public interface MessageService {

   @RSocketExchange("greeting/{name}")
   Mono<String> sendMessage(@DestinationVariable("name") String name, @Payload Mono<String> greetingMono);
}