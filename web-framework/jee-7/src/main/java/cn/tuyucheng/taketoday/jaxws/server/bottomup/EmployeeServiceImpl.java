package cn.tuyucheng.taketoday.jaxws.server.bottomup;

import cn.tuyucheng.taketoday.jaxws.server.bottomup.exception.EmployeeAlreadyExists;
import cn.tuyucheng.taketoday.jaxws.server.bottomup.exception.EmployeeNotFound;
import cn.tuyucheng.taketoday.jaxws.server.bottomup.model.Employee;
import cn.tuyucheng.taketoday.jaxws.server.repository.EmployeeRepository;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService(serviceName = "EmployeeService", endpointInterface = "com.tuyucheng.jaxws.server.bottomup.EmployeeService")
public class EmployeeServiceImpl implements EmployeeService {

   @Inject
   private EmployeeRepository employeeRepositoryImpl;

   @WebMethod
   public Employee getEmployee(int id) throws EmployeeNotFound {
      return employeeRepositoryImpl.getEmployee(id);
   }

   @WebMethod
   public Employee updateEmployee(int id, String name) throws EmployeeNotFound {
      return employeeRepositoryImpl.updateEmployee(id, name);
   }

   @WebMethod
   public boolean deleteEmployee(int id) throws EmployeeNotFound {
      return employeeRepositoryImpl.deleteEmployee(id);
   }

   @WebMethod
   public Employee addEmployee(int id, String name) throws EmployeeAlreadyExists {
      return employeeRepositoryImpl.addEmployee(id, name);
   }

   @WebMethod
   public int countEmployees() {
      return employeeRepositoryImpl.count();
   }

   @WebMethod
   public List<Employee> getAllEmployees() {
      return employeeRepositoryImpl.getAllEmployees();
   }
}
