package cn.tuyucheng.taketoday.boot.documentation.springwolf.service;

import cn.tuyucheng.taketoday.boot.documentation.springwolf.adapter.outgoing.OutgoingProducer;
import cn.tuyucheng.taketoday.boot.documentation.springwolf.dto.IncomingPayloadDto;
import cn.tuyucheng.taketoday.boot.documentation.springwolf.dto.OutgoingPayloadDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProcessorService {

   private final OutgoingProducer outgoingProducer;

   public void doHandle(IncomingPayloadDto payload) {
      OutgoingPayloadDto message = OutgoingPayloadDto.builder()
            .foo("Foo message")
            .incomingWrapped(payload)
            .build();

      outgoingProducer.publish(message);
   }
}