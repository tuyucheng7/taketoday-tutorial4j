package cn.tuyucheng.taketoday.xmlapplicationcontext.service;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import cn.tuyucheng.taketoday.xmlapplicationcontext.domain.Employee;

@ExcludeFromJacocoGeneratedReport
public class EmployeeServiceImpl implements EmployeeService {

	@Override
	public Employee getEmployee() {
		return new Employee("Tuyucheng", "Admin");
	}
}