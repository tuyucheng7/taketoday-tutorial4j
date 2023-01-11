package cn.tuyucheng.taketoday.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.tuyucheng.taketoday.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}