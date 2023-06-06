package cn.tuyucheng.taketoday.messaging.postgresql.repository;

import org.springframework.data.repository.CrudRepository;

import cn.tuyucheng.taketoday.messaging.postgresql.domain.Order;

public interface OrdersRepository extends CrudRepository<Order, Long> {
}