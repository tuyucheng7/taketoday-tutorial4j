package cn.tuyucheng.taketoday;

public class EmployeeCreationRequest {

   private String empName;

   private String empNumber;

   public String getEmpName() {
      return empName;
   }

   public void setEmpName(String empName) {
      this.empName = empName;
   }

   @Override
   public String toString() {
      return "cn.tuyucheng.taketoday.EmployeeCreationRequest{" + "employeename='" + empName + '\'' + '}';
   }

   public String getEmpNumber() {
      return empNumber;
   }

   public void setEmpNumber(String empNumber) {
      this.empNumber = empNumber;
   }
}