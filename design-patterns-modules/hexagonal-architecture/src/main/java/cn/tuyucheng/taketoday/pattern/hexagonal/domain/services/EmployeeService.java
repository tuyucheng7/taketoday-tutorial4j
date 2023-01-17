package cn.tuyucheng.taketoday.pattern.hexagonal.domain.services;

import cn.tuyucheng.taketoday.pattern.hexagonal.domain.model.Employee;

public interface EmployeeService {

	Employee addEmployee(Employee employee);

	Employee getEmployee(String employeeId);
}
