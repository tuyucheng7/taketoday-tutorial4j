package cn.tuyucheng.taketoday.spring.cloud.connectors.heroku.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
