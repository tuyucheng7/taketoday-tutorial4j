package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.domain.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepository extends ReactiveMongoRepository<Order, ObjectId> {

}
