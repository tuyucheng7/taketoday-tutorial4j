package cn.tuyucheng.taketoday.ecommerce.repository;

import cn.tuyucheng.taketoday.ecommerce.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
