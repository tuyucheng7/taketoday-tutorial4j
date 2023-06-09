package cn.tuyucheng.taketoday.mockito.argumentmatchers.service;

import cn.tuyucheng.taketoday.mockito.argumentmatchers.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

   public Message deliverMessage(Message message) {
      return message;
   }
}