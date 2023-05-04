package cn.tuyucheng.taketoday.examples.r2dbc.flyway.repository;

import java.util.UUID;

import cn.tuyucheng.taketoday.examples.r2dbc.flyway.model.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StudentRepository extends ReactiveCrudRepository<Student, UUID> {
}