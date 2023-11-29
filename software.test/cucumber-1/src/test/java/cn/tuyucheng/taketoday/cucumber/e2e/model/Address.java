package cn.tuyucheng.taketoday.cucumber.e2e.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
   private String streetAddress;
   private String city;
   private String postCode;
   private String state;
   private String country;
   private String county;
}