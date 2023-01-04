package cn.tuyucheng.taketoday.multipartupload;

import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository {
	void saveEmployee(Employee employee);
}