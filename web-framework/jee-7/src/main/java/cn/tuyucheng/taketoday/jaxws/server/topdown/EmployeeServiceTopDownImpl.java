package cn.tuyucheng.taketoday.jaxws.server.topdown;

import cn.tuyucheng.taketoday.jaxws.server.repository.EmployeeRepository;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;


@WebService(name = "EmployeeServiceTopDown", targetNamespace = "http://topdown.server.jaxws.tuyucheng.com/", endpointInterface = "com.tuyucheng.jaxws.server.topdown.EmployeeServiceTopDown")
public class EmployeeServiceTopDownImpl implements EmployeeServiceTopDown {

   @Inject
   private EmployeeRepository employeeRepositoryImpl;

   @WebMethod
   public int countEmployees() {
      return employeeRepositoryImpl.count();
   }
}
