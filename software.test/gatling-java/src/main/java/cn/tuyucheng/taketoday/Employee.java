package cn.tuyucheng.taketoday;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Employee {
   private String empName;
   private Address address;
   private String id;
   private Set<String> projects;
}