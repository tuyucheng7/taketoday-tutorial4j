package cn.tuyucheng.taketoday.cucumber.e2e.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {

   private String firstName;
   private String lastName;
   private int age;
   private String emailAddress;
   private Address address;
   private PhoneNumber phoneNumber;
}