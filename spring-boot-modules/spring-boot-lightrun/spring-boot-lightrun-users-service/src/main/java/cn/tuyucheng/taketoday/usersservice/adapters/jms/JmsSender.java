package cn.tuyucheng.taketoday.usersservice.adapters.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JmsSender {
	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendDeleteUserMessage(String userId) {
		jmsTemplate.convertAndSend("deleted_user", userId);
	}
}