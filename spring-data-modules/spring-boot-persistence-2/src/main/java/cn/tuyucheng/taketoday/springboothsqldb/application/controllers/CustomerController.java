package cn.tuyucheng.taketoday.springboothsqldb.application.controllers;

import cn.tuyucheng.taketoday.springboothsqldb.application.entities.Customer;
import cn.tuyucheng.taketoday.springboothsqldb.application.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

	private final CustomerRepository customerRepository;

	@Autowired
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@PostMapping("/customers")
	public Customer addCustomer(@RequestBody Customer customer) {
		customerRepository.save(customer);
		return customer;
	}

	@GetMapping("/customers")
	public List<Customer> getCustomers() {
		return (List<Customer>) customerRepository.findAll();
	}
}