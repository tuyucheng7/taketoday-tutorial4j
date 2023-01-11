package cn.tuyucheng.taketoday.spring.data.jpa.repository;

import cn.tuyucheng.taketoday.spring.data.jpa.entity.Employee;
import net.bytebuddy.TypeCache.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query(value = "SELECT e FROM Employee e")
    List<Employee> findAllEmployees(Sort sort);

    @Query("SELECT e FROM Employee e WHERE e.salary = ?1")
    Employee findAllEmployeesWithSalary(Long salary);

    @Query("SELECT e FROM Employee e WHERE e.name = ?1 and e.salary = ?2")
    Employee findUserByNameAndSalary(String name, Long salary);

    @Query(value = "SELECT * FROM Employee e WHERE e.salary = ?1", nativeQuery = true)
    Employee findUserBySalaryNative(Long salary);

    @Query("SELECT e FROM Employee e WHERE e.name = :name and e.salary = :salary")
    Employee findUserByEmployeeNameAndSalaryNamedParameters(@Param("name") String employeeName,
                                                            @Param("salary") Long employeeSalary);

    @Query(value = "SELECT * FROM Employee e WHERE e.name = :name and e.salary = :salary", nativeQuery = true)
    Employee findUserByNameAndSalaryNamedParamsNative(@Param("name") String employeeName,
                                                      @Param("salary") Long employeeSalary);
}