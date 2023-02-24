package cn.tuyucheng.taketoday.customerservice;

import cn.tuyucheng.taketoday.orderservice.client.OrderClient;
import cn.tuyucheng.taketoday.orderservice.client.OrderDTO;
import cn.tuyucheng.taketoday.orderservice.client.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class CustomerService {

	@Autowired
	private OrderClient orderClient;

	private List<Customer> customers = Arrays.asList(

		new Customer(1, "John", "Smith"),
		new Customer(2, "Deny", "Dominic"));


	@GetMapping
	public List<Customer> getAllCustomers() {
		return customers;
	}

	@GetMapping("/{id}")
	public Customer getCustomerById(@PathVariable int id) {
		return customers.stream()
			.filter(customer -> customer.getId() == id)
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}


	@PostMapping(value = "/order")
	public String sendOrder(@RequestBody Map<String, Object> body) {

		OrderDTO dto = new OrderDTO();
		dto.setCustomerId((Integer) body.get("customerId"));
		dto.setItemId((String) body.get("itemId"));

		OrderResponse response = orderClient.order(dto);

		return response.getStatus();
	}
}