package cn.tuyucheng.taketoday.springamqp.errorhandling.consumer;

import cn.tuyucheng.taketoday.springamqp.errorhandling.configuration.SimpleDLQAmqpConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class SimpleDLQAmqpContainer {
	private static final Logger log = LoggerFactory.getLogger(SimpleDLQAmqpContainer.class);
	private final RabbitTemplate rabbitTemplate;

	public SimpleDLQAmqpContainer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@RabbitListener(queues = SimpleDLQAmqpConfiguration.QUEUE_MESSAGES_DLQ)
	public void processFailedMessages(Message message) {
		log.info("Received failed message: {}", message.toString());
	}

	@RabbitListener(queues = SimpleDLQAmqpConfiguration.QUEUE_MESSAGES_DLQ)
	public void processFailedMessagesRequeue(Message failedMessage) {
		log.info("Received failed message, requeueing: {}", failedMessage.toString());
		log.info("Received failed message, requeueing: {}", failedMessage.getMessageProperties().getReceivedRoutingKey());
		rabbitTemplate.send(SimpleDLQAmqpConfiguration.EXCHANGE_MESSAGES, failedMessage.getMessageProperties().getReceivedRoutingKey(), failedMessage);
	}
}
