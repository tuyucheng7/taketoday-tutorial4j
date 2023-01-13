package cn.tuyucheng.taketoday.async.consumer;

import cn.tuyucheng.taketoday.async.producer.OrderProducer;
import cn.tuyucheng.taketoday.constants.OrderStatus;
import cn.tuyucheng.taketoday.domain.Order;
import cn.tuyucheng.taketoday.reactive.service.ShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderConsumer {

	@Autowired
	ShippingService shippingService;

	@Autowired
	OrderProducer orderProducer;

	@KafkaListener(topics = "orders", groupId = "shipping")
	public void consume(Order order) throws IOException {
		LOGGER.info("Order received to process: {}", order);
		if (OrderStatus.PREPARE_SHIPPING.equals(order.getOrderStatus())) {
			shippingService.handleOrder(order)
				.doOnSuccess(o -> {
					LOGGER.info("Order processed succesfully.");
					orderProducer.sendMessage(order.setOrderStatus(OrderStatus.SHIPPING_SUCCESS)
						.setShippingDate(o.getShippingDate()));
				})
				.doOnError(e -> {
					if (LOGGER.isErrorEnabled())
						LOGGER.error("Order failed to process: " + e);
					orderProducer.sendMessage(order.setOrderStatus(OrderStatus.SHIPPING_FAILURE)
						.setResponseMessage(e.getMessage()));
				})
				.subscribe();
		}
	}
}