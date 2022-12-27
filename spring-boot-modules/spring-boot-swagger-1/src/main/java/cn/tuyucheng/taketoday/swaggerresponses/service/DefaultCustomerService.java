package cn.tuyucheng.taketoday.swaggerresponses.service;

import cn.tuyucheng.taketoday.swaggerresponses.response.CustomerResponse;
import org.springframework.stereotype.Service;

@Service
public class DefaultCustomerService implements CustomerService {
	@Override
	public CustomerResponse getById(Long id) {
		return new CustomerResponse(1L, "Jane", "Doe");
	}
}