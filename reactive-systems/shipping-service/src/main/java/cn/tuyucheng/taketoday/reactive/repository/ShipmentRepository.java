package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.domain.Shipment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShipmentRepository extends ReactiveMongoRepository<Shipment, ObjectId> {

}
