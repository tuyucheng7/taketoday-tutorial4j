package cn.tuyucheng.taketoday.pattern.hexagonal.persistence;

import cn.tuyucheng.taketoday.pattern.hexagonal.domain.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository {

	Employee add(Employee employee);

	Optional<Employee> findById(String id);

}
