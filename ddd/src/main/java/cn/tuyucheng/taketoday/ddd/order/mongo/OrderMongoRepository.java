package cn.tuyucheng.taketoday.ddd.order.mongo;

import cn.tuyucheng.taketoday.ddd.order.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<Order, String> {

}
