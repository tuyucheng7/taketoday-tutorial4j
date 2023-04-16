package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.domain.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {
}