package cn.tuyucheng.taketoday.app.rest;

import cn.tuyucheng.taketoday.app.api.MessageDTO;
import cn.tuyucheng.taketoday.domain.model.Message;
import cn.tuyucheng.taketoday.domain.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@PostMapping
	public Message createMessage(@RequestBody MessageDTO messageDTO) {
		Message message = new Message();
		message.setText(messageDTO.getText());
		message.setFrom(messageDTO.getFrom());
		message.setTo(messageDTO.getTo());
		message.setDate(Date.from(Instant.now()));
		message.setId(UUID.randomUUID());

		return messageService.deliverMessage(message);
	}
}