package cn.tuyucheng.taketoday.spring.data.couchbase.repos;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, String>, CustomStudentRepository {
    List<Student> findByFirstName(String firstName);

    List<Student> findByLastName(String lastName);
}