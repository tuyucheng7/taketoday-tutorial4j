package cn.tuyucheng.taketoday.pattern.hexagonal.persistence;

import cn.tuyucheng.taketoday.pattern.hexagonal.domain.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRepoEx extends MongoRepository<Employee, String> {
}
