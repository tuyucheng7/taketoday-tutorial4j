package cn.tuyucheng.taketoday.instancio.student.model;

import cn.tuyucheng.taketoday.instancio.util.PrettyToString;

import java.util.List;

public class ContactInfo {
   private Address address;
   private List<Phone> phones;
   private String email;

   public Address getAddress() {
      return address;
   }

   public List<Phone> getPhones() {
      return phones;
   }

   public String getEmail() {
      return email;
   }

   @Override
   public String toString() {
      return PrettyToString.toPrettyString(this);
   }
}