package cn.tuyucheng.taketoday.spring.reactive.performance.virtualthreads;

import cn.tuyucheng.taketoday.spring.reactive.performance.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

interface ProductRepository extends MongoRepository<Product, String> {
}