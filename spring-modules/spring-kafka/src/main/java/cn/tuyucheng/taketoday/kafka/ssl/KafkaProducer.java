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
			.whenComplete((result, ex) -> {
				if (ex == null) {
					LOGGER.info("Message sent to topic: {}", message);
				} else {
					LOGGER.error("Failed to send message", ex);
				}
			});
	}
}