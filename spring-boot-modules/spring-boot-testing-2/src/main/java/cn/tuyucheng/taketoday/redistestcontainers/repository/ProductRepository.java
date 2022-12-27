package cn.tuyucheng.taketoday.redistestcontainers.repository;

import cn.tuyucheng.taketoday.redistestcontainers.hash.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
}