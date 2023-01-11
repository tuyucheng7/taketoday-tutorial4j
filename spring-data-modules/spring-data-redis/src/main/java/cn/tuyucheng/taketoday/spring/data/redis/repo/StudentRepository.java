package cn.tuyucheng.taketoday.spring.data.redis.repo;

import cn.tuyucheng.taketoday.spring.data.redis.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, String> {
}
