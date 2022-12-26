package cn.tuyucheng.taketoday.antmatchers.controllers;

import cn.tuyucheng.taketoday.antmatchers.dtos.Customer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

	@GetMapping("/customers/{id}")
	public Customer getCustomerById(@PathVariable("id") String id) {
		return new Customer("Customer 1", "Address 1", "Phone 1");
	}
}