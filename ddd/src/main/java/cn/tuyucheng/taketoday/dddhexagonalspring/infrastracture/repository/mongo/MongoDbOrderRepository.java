package cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.repository.mongo;

import cn.tuyucheng.taketoday.dddhexagonalspring.domain.Order;
import cn.tuyucheng.taketoday.dddhexagonalspring.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Primary
public class MongoDbOrderRepository implements OrderRepository {

	private final SpringDataMongoOrderRepository orderRepository;

	@Autowired
	public MongoDbOrderRepository(final SpringDataMongoOrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public Optional<Order> findById(final UUID id) {
		return orderRepository.findById(id);
	}

	@Override
	public void save(final Order order) {
		orderRepository.save(order);
	}
}
