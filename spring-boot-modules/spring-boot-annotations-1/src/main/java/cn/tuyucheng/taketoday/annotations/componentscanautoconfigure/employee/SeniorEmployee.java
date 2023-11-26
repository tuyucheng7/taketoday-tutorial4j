package cn.tuyucheng.taketoday.annotations.componentscanautoconfigure.employee;

import org.springframework.stereotype.Component;

@Component
public class SeniorEmployee {

   @Override
   public String toString() {
      return STR."Senior Employee\{this.hashCode()}";
   }
}