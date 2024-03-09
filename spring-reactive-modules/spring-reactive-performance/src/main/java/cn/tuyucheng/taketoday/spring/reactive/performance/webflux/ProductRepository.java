package cn.tuyucheng.taketoday.spring.reactive.performance.webflux;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import cn.tuyucheng.taketoday.spring.reactive.performance.model.Product;

interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}