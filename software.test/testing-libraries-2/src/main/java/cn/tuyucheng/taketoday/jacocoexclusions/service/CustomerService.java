package cn.tuyucheng.taketoday.jacocoexclusions.service;

import cn.tuyucheng.taketoday.jacocoexclusions.generated.Generated;

public class CustomerService {

   @Generated
   public CustomerService() {
      // constructor excluded form coverage report
   }

   // this method will be excluded from coverage due to @Generated.
   @Generated
   public String getProductId() {
      return "An ID";
   }

   public String getCustomerName() {
      return "some name";
   }
}