package cn.tuyucheng.taketoday.async.consumer;

import cn.tuyucheng.taketoday.async.producer.OrderProducer;
import cn.tuyucheng.taketoday.constants.OrderStatus;
import cn.tuyucheng.taketoday.domain.Order;
import cn.tuyucheng.taketoday.reactive.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderConsumer {

	@Autowired
	ProductService productService;

	@Autowired
	OrderProducer orderProducer;

	@KafkaListener(topics = "orders", groupId = "inventory")
	public void consume(Order order) throws IOException {
		LOGGER.info("Order received to process: {}", order);
		if (OrderStatus.RESERVE_INVENTORY.equals(order.getOrderStatus())) {
			productService.handleOrder(order)
				.doOnSuccess(o -> {
					LOGGER.info("Order processed succesfully.");
					orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_SUCCESS));
				})
				.doOnError(e -> {
					if (LOGGER.isDebugEnabled())
						LOGGER.error("Order failed to process: " + e);
					orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_FAILURE)
						.setResponseMessage(e.getMessage()));
				})
				.subscribe();
		} else if (OrderStatus.REVERT_INVENTORY.equals(order.getOrderStatus())) {
			productService.revertOrder(order)
				.doOnSuccess(o -> {
					LOGGER.info("Order reverted succesfully.");
					orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_REVERT_SUCCESS));
				})
				.doOnError(e -> {
					if (LOGGER.isDebugEnabled())
						LOGGER.error("Order failed to revert: " + e);
					orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_REVERT_FAILURE)
						.setResponseMessage(e.getMessage()));
				})
				.subscribe();
		}
	}
}