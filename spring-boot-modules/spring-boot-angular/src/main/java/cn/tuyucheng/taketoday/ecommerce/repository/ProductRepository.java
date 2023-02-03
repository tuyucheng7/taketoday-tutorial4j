package cn.tuyucheng.taketoday.ecommerce.repository;

import cn.tuyucheng.taketoday.ecommerce.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
