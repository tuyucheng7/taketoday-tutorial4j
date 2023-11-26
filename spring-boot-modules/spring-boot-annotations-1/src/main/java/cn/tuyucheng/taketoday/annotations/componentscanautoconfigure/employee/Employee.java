package cn.tuyucheng.taketoday.annotations.componentscanautoconfigure.employee;

import org.springframework.stereotype.Component;

@Component("employee")
public class Employee {

   @Override
   public String toString() {
      return STR."Employee\{this.hashCode()}";
   }
}