package cn.tuyucheng.taketoday.boot.repository;

import cn.tuyucheng.taketoday.boot.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}