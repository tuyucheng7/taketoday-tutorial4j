package cn.tuyucheng.taketoday.multitenant.controller;

import cn.tuyucheng.taketoday.multitenant.domain.Employee;
import cn.tuyucheng.taketoday.multitenant.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepository;

	@PostMapping(path = "/employee")
	public ResponseEntity<?> createEmployee() {
		Employee newEmployee = new Employee();
		newEmployee.setName("Baeldung");
		employeeRepository.save(newEmployee);
		return ResponseEntity.ok(newEmployee);
	}
}