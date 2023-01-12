package cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.repository.cassandra;

import cn.tuyucheng.taketoday.dddhexagonalspring.domain.Order;
import cn.tuyucheng.taketoday.dddhexagonalspring.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CassandraDbOrderRepository implements OrderRepository {

	private final SpringDataCassandraOrderRepository orderRepository;

	@Autowired
	public CassandraDbOrderRepository(SpringDataCassandraOrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public Optional<Order> findById(UUID id) {
		Optional<OrderEntity> orderEntity = orderRepository.findById(id);
		if (orderEntity.isPresent()) {
			return Optional.of(orderEntity.get()
				.toOrder());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void save(Order order) {
		orderRepository.save(new OrderEntity(order));
	}

}
