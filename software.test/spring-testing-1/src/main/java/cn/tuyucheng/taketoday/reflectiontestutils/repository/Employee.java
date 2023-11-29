package cn.tuyucheng.taketoday.reflectiontestutils.repository;

public class Employee {
   private Integer id;
   private String name;

   private String employeeToString() {
      return "id: " + getId() + "; name: " + getName();
   }

   public Integer getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}