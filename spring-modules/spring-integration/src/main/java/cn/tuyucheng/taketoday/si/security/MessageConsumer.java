package cn.tuyucheng.taketoday.si.security;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class MessageConsumer {

	private String messageContent;

	private Map<String, String> messagePSContent = new ConcurrentHashMap<>();

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public Map<String, String> getMessagePSContent() {
		return messagePSContent;
	}

	public void setMessagePSContent(Map<String, String> messagePSContent) {
		this.messagePSContent = messagePSContent;
	}

	@ServiceActivator(inputChannel = "endDirectChannel")
	public void endDirectFlow(Message<?> message) {
		setMessageContent(message.getPayload().toString());
	}

	@ServiceActivator(inputChannel = "finalPSResult")
	public void endPSFlow(Message<?> message) {
		Logger.getAnonymousLogger().info(Thread.currentThread().getName() + " has completed ---------------------------");
		messagePSContent.put(Thread.currentThread().getName(), (String) message.getPayload());
	}

}
