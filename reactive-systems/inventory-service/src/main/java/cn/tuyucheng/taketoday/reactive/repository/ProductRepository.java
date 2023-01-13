package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.domain.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, ObjectId> {

}
