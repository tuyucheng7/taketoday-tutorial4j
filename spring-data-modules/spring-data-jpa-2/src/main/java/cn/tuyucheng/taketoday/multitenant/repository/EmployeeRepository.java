package cn.tuyucheng.taketoday.multitenant.repository;

import cn.tuyucheng.taketoday.multitenant.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}