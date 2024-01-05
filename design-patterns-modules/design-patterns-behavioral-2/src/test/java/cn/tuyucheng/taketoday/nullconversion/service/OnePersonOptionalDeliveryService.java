package cn.tuyucheng.taketoday.nullconversion.service;

import cn.tuyucheng.taketoday.nullconversion.Address;
import cn.tuyucheng.taketoday.nullconversion.Delivery;
import cn.tuyucheng.taketoday.nullconversion.Person;
import cn.tuyucheng.taketoday.nullconversion.ZipCode;

import java.util.Optional;

public class OnePersonOptionalDeliveryService extends MockOnePersonDeliveryServiceBase {

   public OnePersonOptionalDeliveryService(Person person) {
      super(person);
   }

   @Override
   public Delivery calculateDeliveryForPerson(Long id) {
      return Optional.ofNullable(getPersonById(id))
            .map(Person::getAddress)
            .map(Address::getZipCode)
            .map(ZipCode::getCode)
            .map(this::calculateDeliveryForZipCode)
            .orElse(null);
   }
}