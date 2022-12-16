package cn.tuyucheng.taketoday.dip.tests;

import cn.tuyucheng.taketoday.dip.daoimplementations.SimpleCustomerDao;
import cn.tuyucheng.taketoday.dip.entities.Customer;
import cn.tuyucheng.taketoday.dip.services.CustomerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerServiceUnitTest {

	private CustomerService customerService;

	@BeforeEach
	void setUpCustomerServiceInstance() {
		Map<Integer, Customer> customers = new HashMap<>();
		customers.put(1, new Customer("John"));
		customers.put(2, new Customer("Susan"));
		customerService = new CustomerService(new SimpleCustomerDao(customers));
	}

	@Test
	void givenCustomerServiceInstance_whenCalledFindById_thenCorrect() {
		Assertions.assertThat(customerService.findById(1)).isInstanceOf(Optional.class);
	}

	@Test
	void givenCustomerServiceInstance_whenCalledFindAll_thenCorrect() {
		Assertions.assertThat(customerService.findAll()).isInstanceOf(List.class);
	}

	@Test
	void givenCustomerServiceInstance_whenCalledFindByIdWithNullCustomer_thenCorrect() {
		Map<Integer, Customer> customers = new HashMap<>();
		customers.put(1, null);
		customerService = new CustomerService(new SimpleCustomerDao(customers));

		Customer customer = customerService.findById(1).orElseGet(() -> new Customer("Non-existing customer"));

		assertThat(customer.getName()).isEqualTo("Non-existing customer");
	}
}