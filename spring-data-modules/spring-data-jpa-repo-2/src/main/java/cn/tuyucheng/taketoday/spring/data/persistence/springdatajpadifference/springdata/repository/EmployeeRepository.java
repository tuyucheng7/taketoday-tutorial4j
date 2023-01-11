package cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.springdata.repository;

import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.model.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFirstName(String firstName);

    @Query(value = "SELECT e FROM Employee e")
    List<Employee> findAllEmployee(Sort sort);
}