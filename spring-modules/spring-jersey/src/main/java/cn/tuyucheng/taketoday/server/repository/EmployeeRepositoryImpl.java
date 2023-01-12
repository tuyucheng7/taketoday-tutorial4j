package cn.tuyucheng.taketoday.server.repository;

import cn.tuyucheng.taketoday.server.exception.EmployeeAlreadyExists;
import cn.tuyucheng.taketoday.server.exception.EmployeeNotFound;
import cn.tuyucheng.taketoday.server.model.Employee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {
	private List<Employee> employeeList;

	public EmployeeRepositoryImpl() {
		employeeList = new ArrayList<Employee>();
		employeeList.add(new Employee(1, "Jane"));
		employeeList.add(new Employee(2, "Jack"));
		employeeList.add(new Employee(3, "George"));
	}

	public List<Employee> getAllEmployees() {
		return employeeList;
	}

	public Employee getEmployee(int id) {
		for (Employee emp : employeeList) {
			if (emp.getId() == id) {
				return emp;
			}
		}
		throw new EmployeeNotFound();
	}

	public void updateEmployee(Employee employee, int id) {
		for (Employee emp : employeeList) {
			if (emp.getId() == id) {
				emp.setId(employee.getId());
				emp.setFirstName(employee.getFirstName());
				return;
			}
		}
		throw new EmployeeNotFound();
	}

	public void deleteEmployee(int id) {
		for (Employee emp : employeeList) {
			if (emp.getId() == id) {
				employeeList.remove(emp);
				return;
			}
		}
		throw new EmployeeNotFound();
	}

	public void addEmployee(Employee employee) {
		for (Employee emp : employeeList) {
			if (emp.getId() == employee.getId()) {
				throw new EmployeeAlreadyExists();
			}
		}
		employeeList.add(employee);
	}
}
