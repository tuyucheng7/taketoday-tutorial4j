package cn.tuyucheng.taketoday.pattern.hexagonal.config;

import cn.tuyucheng.taketoday.pattern.hexagonal.domain.services.EmployeeService;
import cn.tuyucheng.taketoday.pattern.hexagonal.domain.services.EmployeeServiceImpl;
import cn.tuyucheng.taketoday.pattern.hexagonal.persistence.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	@Bean
	public EmployeeService getEmployeeService(EmployeeRepository employeeRepository) {
		return new EmployeeServiceImpl(employeeRepository);
	}
}
