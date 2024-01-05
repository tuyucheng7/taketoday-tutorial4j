package cn.tuyucheng.taketoday.nullconversion.service;

import cn.tuyucheng.taketoday.nullconversion.Address;
import cn.tuyucheng.taketoday.nullconversion.Delivery;
import cn.tuyucheng.taketoday.nullconversion.Person;
import cn.tuyucheng.taketoday.nullconversion.ZipCode;
import com.google.common.base.Optional;

public class OnePersonGuavaOptionalDeliveryService extends MockOnePersonDeliveryServiceBase {

   public OnePersonGuavaOptionalDeliveryService(Person person) {
      super(person);
   }

   @Override
   public Delivery calculateDeliveryForPerson(Long id) {
      return Optional.fromNullable(getPersonById(id))
            .transform(Person::getAddress)
            .transform(Address::getZipCode)
            .transform(ZipCode::getCode)
            .transform(this::calculateDeliveryForZipCode)
            .orNull();
   }
}