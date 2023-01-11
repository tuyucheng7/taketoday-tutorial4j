package cn.tuyucheng.taketoday.spring.data.keyvalue.services;

import cn.tuyucheng.taketoday.spring.data.keyvalue.vo.Employee;

import java.util.Optional;

public interface EmployeeService {

	void save(Employee employee);

	Optional<Employee> get(Integer id);

	Iterable<Employee> fetchAll();

	void update(Employee employee);

	void delete(Integer id);

	Iterable<Employee> getSortedListOfEmployeesBySalary();

}
