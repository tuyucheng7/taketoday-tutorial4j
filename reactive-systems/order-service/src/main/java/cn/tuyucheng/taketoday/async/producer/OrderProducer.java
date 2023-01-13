package cn.tuyucheng.taketoday.async.producer;

import cn.tuyucheng.taketoday.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderProducer {

	@Autowired
	private KafkaTemplate<String, Order> kafkaTemplate;

	public void sendMessage(Order order) {
		LOGGER.info("Order processed to dispatch: {}", order);
		this.kafkaTemplate.send("orders", order);
	}

}
