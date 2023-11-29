package cn.tuyucheng.taketoday.reflectiontestutils.repository;

import org.springframework.stereotype.Component;

@Component
public class HRService {

   public String getEmployeeStatus(Integer employeeId) {
      return "Inactive";
   }
}