package cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.repository.mongo;

import cn.tuyucheng.taketoday.dddhexagonalspring.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataMongoOrderRepository extends MongoRepository<Order, UUID> {
}
