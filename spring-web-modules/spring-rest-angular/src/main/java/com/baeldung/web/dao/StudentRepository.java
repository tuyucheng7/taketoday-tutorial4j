package cn.tuyucheng.taketoday.web.dao;

import cn.tuyucheng.taketoday.web.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long>
{

}
