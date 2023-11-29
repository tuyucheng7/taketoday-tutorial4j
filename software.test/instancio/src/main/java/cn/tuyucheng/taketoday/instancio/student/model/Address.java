package cn.tuyucheng.taketoday.instancio.student.model;

import cn.tuyucheng.taketoday.instancio.util.PrettyToString;

public class Address {
   private String street;
   private String city;
   private String country;

   public String getStreet() {
      return street;
   }

   public String getCity() {
      return city;
   }

   public String getCountry() {
      return country;
   }

   public void setCountry(final String country) {
      this.country = country;
   }

   @Override
   public String toString() {
      return PrettyToString.toPrettyString(this);
   }
}