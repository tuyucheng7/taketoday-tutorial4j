package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.entity.Address;
import cn.tuyucheng.taketoday.entity.Customer;
import cn.tuyucheng.taketoday.entity.DeliveryAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DeliveryAddressMapper {

	@Mapping(source = "customer.firstName", target = "forename")
	@Mapping(source = "customer.lastName", target = "surname")
	@Mapping(source = "address.street", target = "street")
	@Mapping(source = "address.postalcode", target = "postalcode")
	@Mapping(source = "address.county", target = "county")
	DeliveryAddress from(Customer customer, Address address);

	@Mapping(source = "address.postalcode", target = "postalcode")
	@Mapping(source = "address.county", target = "county")
	DeliveryAddress updateAddress(@MappingTarget DeliveryAddress deliveryAddress, Address address);
}
