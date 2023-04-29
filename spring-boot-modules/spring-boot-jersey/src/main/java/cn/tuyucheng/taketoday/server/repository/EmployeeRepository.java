package cn.tuyucheng.taketoday.server.repository;

import cn.tuyucheng.taketoday.server.model.Employee;

import java.util.List;

public interface EmployeeRepository {

	public List<Employee> getAllEmployees();

	public Employee getEmployee(int id);

	public void updateEmployee(Employee employee, int id);

	public void deleteEmployee(int id);

	public void addEmployee(Employee employee);
}
