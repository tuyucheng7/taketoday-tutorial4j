package cn.tuyucheng.taketoday.springcassandra.repository;

import cn.tuyucheng.taketoday.springcassandra.model.Car;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository extends CassandraRepository<Car, UUID> {

}
