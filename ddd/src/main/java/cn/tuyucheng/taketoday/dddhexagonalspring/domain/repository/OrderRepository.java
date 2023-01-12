package cn.tuyucheng.taketoday.dddhexagonalspring.domain.repository;

import cn.tuyucheng.taketoday.dddhexagonalspring.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
	Optional<Order> findById(UUID id);

	void save(Order order);
}
