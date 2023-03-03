package cn.tuyucheng.taketoday.xmlapplicationcontext.service;

import cn.tuyucheng.taketoday.xmlapplicationcontext.domain.Employee;

public class EmployeeServiceTestImpl implements EmployeeService {

	@Override
	public Employee getEmployee() {
		return new Employee("Tuyucheng-Test", "Admin");
	}
}