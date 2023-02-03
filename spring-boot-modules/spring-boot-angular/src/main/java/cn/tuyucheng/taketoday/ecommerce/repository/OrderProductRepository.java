package cn.tuyucheng.taketoday.ecommerce.repository;

import cn.tuyucheng.taketoday.ecommerce.model.OrderProduct;
import cn.tuyucheng.taketoday.ecommerce.model.OrderProductPK;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductRepository extends CrudRepository<OrderProduct, OrderProductPK> {
}
