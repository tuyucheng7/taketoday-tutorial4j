package cn.tuyucheng.taketoday.spring.service;

import cn.tuyucheng.taketoday.spring.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {

    List<Employee> getEmployeeList();
}
