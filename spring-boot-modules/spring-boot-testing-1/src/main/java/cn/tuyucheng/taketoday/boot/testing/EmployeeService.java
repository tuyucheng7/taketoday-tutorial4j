package cn.tuyucheng.taketoday.boot.testing;

import java.util.List;

public interface EmployeeService {

	Employee getEmployeeById(Long id);

	Employee getEmployeeByName(String name);

	List<Employee> getAllEmployees();

	boolean exists(String email);

	Employee save(Employee employee);
}