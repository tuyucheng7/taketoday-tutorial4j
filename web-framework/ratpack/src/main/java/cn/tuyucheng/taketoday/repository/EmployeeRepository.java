package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.model.Employee;
import ratpack.exec.Promise;

public interface EmployeeRepository {

	Promise<Employee> findEmployeeById(Long id) throws Exception;

}
