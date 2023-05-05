package cn.tuyucheng.taketoday.springcassandra.repository;

import cn.tuyucheng.taketoday.springcassandra.model.Person;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonRepository extends CassandraRepository<Person, UUID> {
}