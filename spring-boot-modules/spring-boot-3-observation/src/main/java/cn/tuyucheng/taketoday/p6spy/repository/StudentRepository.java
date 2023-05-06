package cn.tuyucheng.taketoday.p6spy.repository;

import cn.tuyucheng.taketoday.p6spy.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
   List<Student> findAllByFirstName(String firstName);
}