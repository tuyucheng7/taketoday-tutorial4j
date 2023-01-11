package cn.tuyucheng.taketoday.mongodb.dbref.repository;

import cn.tuyucheng.taketoday.mongodb.dbref.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<Person, String> {
}