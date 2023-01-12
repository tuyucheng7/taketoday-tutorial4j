package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.CustomerDto;
import cn.tuyucheng.taketoday.entity.Customer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.assertEquals;

public class CustomerDtoMapperUnitTest {

	private CustomerDtoMapper customerDtoMapper = Mappers.getMapper(CustomerDtoMapper.class);

	@Test
	void testGivenCustomer_mapsToCustomerDto() {

		// given
		Customer customer = new Customer().setFirstName("Max")
			.setLastName("Powers");

		// when
		CustomerDto customerDto = customerDtoMapper.from(customer);

		// then
		assertEquals(customerDto.getForename(), customer.getFirstName());
		assertEquals(customerDto.getSurname(), customer.getLastName());
	}
}
