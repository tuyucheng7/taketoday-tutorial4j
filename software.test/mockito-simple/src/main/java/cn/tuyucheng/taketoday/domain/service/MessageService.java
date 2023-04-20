package cn.tuyucheng.taketoday.domain.service;

import cn.tuyucheng.taketoday.domain.model.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	public Message deliverMessage(Message message) {
		return message;
	}
}