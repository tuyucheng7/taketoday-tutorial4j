package cn.tuyucheng.taketoday.seda.springintegration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.Map;

@MessagingGateway
public interface TestGateway {
	@Gateway(requestChannel = "receiveTextChannel", replyChannel = "returnResponseChannel")
	Map<String, Long> countWords(String test);
}