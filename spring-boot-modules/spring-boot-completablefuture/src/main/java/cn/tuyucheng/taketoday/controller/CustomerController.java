package cn.tuyucheng.taketoday.controller;

import cn.tuyucheng.taketoday.dto.CustomerResponse;
import cn.tuyucheng.taketoday.dto.UpdateCustomerRequest;
import cn.tuyucheng.taketoday.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

	private final CustomerService customerService;

	@GetMapping
	public List<CustomerResponse> getCustomers() {
		return customerService.getAllCustomers();
	}

	@PutMapping("/{customerId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void replaceCustomer(
		@PathVariable Integer customerId,
		@RequestBody UpdateCustomerRequest request
	) {
		customerService.replaceCustomer(customerId, request);
	}

	@PatchMapping("/{customerId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateCustomer(@PathVariable Integer customerId,
							   @RequestBody UpdateCustomerRequest request) {
		customerService.updateCustomer(customerId, request);
	}

	@GetMapping("/{customerId}")
	public CustomerResponse getCustomerById(@PathVariable Integer customerId) {
		return customerService.getCustomerById(customerId);
	}
}