package cn.tuyucheng.taketoday.boot.web.controllers;

import cn.tuyucheng.taketoday.boot.daos.CustomerRepository;
import cn.tuyucheng.taketoday.boot.domain.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class CustomerController {

	CustomerRepository customerRepository;

	public CustomerController(CustomerRepository customerRepository2) {
		this.customerRepository = customerRepository2;
	}

	@PostMapping("/customers")
	public ResponseEntity<List<Customer>> insertCustomers() {
		Customer c1 = new Customer("James", "Gosling");
		Customer c2 = new Customer("Doug", "Lea");
		Customer c3 = new Customer("Martin", "Fowler");
		Customer c4 = new Customer("Brian", "Goetz");
		List<Customer> customers = Arrays.asList(c1, c2, c3, c4);
		customerRepository.saveAll(customers);
		return ResponseEntity.ok(customers);
	}
}