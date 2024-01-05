package cn.tuyucheng.taketoday.nullconversion.service;

import cn.tuyucheng.taketoday.nullconversion.Address;
import cn.tuyucheng.taketoday.nullconversion.Delivery;
import cn.tuyucheng.taketoday.nullconversion.DeliveryService;
import cn.tuyucheng.taketoday.nullconversion.Person;
import cn.tuyucheng.taketoday.nullconversion.ZipCode;

import java.util.Optional;

public abstract class MockOnePersonDeliveryServiceBase implements DeliveryService {

   private final Person person;

   public MockOnePersonDeliveryServiceBase(Person person) {
      this.person = person;
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

   public Delivery calculateDeliveryForPersonWithOptional(Long id) {
      return Optional.ofNullable(getPersonById(id))
            .map(Person::getAddress)
            .map(Address::getZipCode)
            .map(ZipCode::getCode)
            .map(this::calculateDeliveryForZipCode)
            .orElse(null);
   }

   protected Person getPersonById(Long id) {
      return person;
   }

   protected Delivery calculateDeliveryForZipCode(String zipCode) {
      if (zipCode == null || zipCode.isEmpty()) {
         return null;
      } else {
         return Delivery.freeDelivery();
      }
   }
}