package cn.tuyucheng.taketoday.nullconversion.service;

import cn.tuyucheng.taketoday.nullconversion.Delivery;
import cn.tuyucheng.taketoday.nullconversion.Person;
import cn.tuyucheng.taketoday.nullconversion.ZipCode;

public class OnePersonExplicitDeliveryService extends MockOnePersonDeliveryServiceBase {

   public OnePersonExplicitDeliveryService(Person person) {
      super(person);
   }

   @Override
   public Delivery calculateDeliveryForPerson(Long id) {
      Person person = getPersonById(id);
      if (person != null && person.getAddress() != null && person.getAddress().getZipCode() != null) {
         ZipCode zipCode = person.getAddress().getZipCode();
         String code = zipCode.getCode();
         return calculateDeliveryForZipCode(code);
      }
      return null;
   }
}