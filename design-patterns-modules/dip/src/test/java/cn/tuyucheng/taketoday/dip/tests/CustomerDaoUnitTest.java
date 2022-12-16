package cn.tuyucheng.taketoday.dip.tests;

import cn.tuyucheng.taketoday.dip.daoimplementations.SimpleCustomerDao;
import cn.tuyucheng.taketoday.dip.daointerfaces.CustomerDao;
import cn.tuyucheng.taketoday.dip.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDaoUnitTest {

	private CustomerDao customerDao;

	@BeforeEach
	void setUpCustomerDaoInstance() {
		Map<Integer, Customer> customers = new HashMap<>();
		customers.put(1, new Customer("John"));
		customers.put(2, new Customer("Susan"));
		customerDao = new SimpleCustomerDao(customers);
	}

	@Test
	void givenCustomerDaoInstance_whenCalledFindById_thenCorrect() {
		assertThat(customerDao.findById(1)).isInstanceOf(Optional.class);
	}

	@Test
	void givenCustomerDaoInstance_whenCalledFindAll_thenCorrect() {
		assertThat(customerDao.findAll()).isInstanceOf(List.class);
	}

	@Test
	void givenCustomerDaoInstance_whenCalledFindByIdWithNullCustomer_thenCorrect() {
		Map<Integer, Customer> customers = new HashMap<Integer, Customer>();
		customers.put(1, null);
		CustomerDao customerDaoObject = new SimpleCustomerDao(customers);

		Customer customer = customerDaoObject.findById(1).orElseGet(() -> new Customer("Non-existing customer"));

		assertThat(customer.getName()).isEqualTo("Non-existing customer");
	}
}