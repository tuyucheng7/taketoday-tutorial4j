package cn.tuyucheng.taketoday.spring.controller;

import cn.tuyucheng.taketoday.spring.domain.Employee;
import cn.tuyucheng.taketoday.spring.exception.InvalidRequestException;
import cn.tuyucheng.taketoday.spring.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/api")
public class RequestMethodController {

    @Autowired
    EmployeeService service;

    @RequestMapping(value = "/employees", produces = "application/json", method={RequestMethod.GET,RequestMethod.POST})
    public List<Employee> findEmployees()
            throws InvalidRequestException {
        return service.getEmployeeList();
    }
}
