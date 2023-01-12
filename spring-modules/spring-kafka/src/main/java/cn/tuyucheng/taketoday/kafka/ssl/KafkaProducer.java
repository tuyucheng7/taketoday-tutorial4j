package cn.tuyucheng.taketoday.kafka.ssl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class KafkaProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message, String topic) {
		LOGGER.info("Producing message: {}", message);
		kafkaTemplate.send(topic, "key", message)
			.addCallback(
				result -> LOGGER.info("Message sent to topic: {}", message),
				ex -> LOGGER.error("Failed to send message", ex)
			);
	}
}