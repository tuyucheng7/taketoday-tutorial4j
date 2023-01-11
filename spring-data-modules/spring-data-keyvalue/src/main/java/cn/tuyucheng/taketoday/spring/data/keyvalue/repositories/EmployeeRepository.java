package cn.tuyucheng.taketoday.spring.data.keyvalue.repositories;

import cn.tuyucheng.taketoday.spring.data.keyvalue.vo.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("employeeRepository")
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
}