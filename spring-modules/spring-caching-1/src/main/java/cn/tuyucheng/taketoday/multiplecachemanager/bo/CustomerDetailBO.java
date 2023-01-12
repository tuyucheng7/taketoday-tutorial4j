package cn.tuyucheng.taketoday.multiplecachemanager.bo;

import cn.tuyucheng.taketoday.multiplecachemanager.entity.Customer;
import cn.tuyucheng.taketoday.multiplecachemanager.entity.Order;
import cn.tuyucheng.taketoday.multiplecachemanager.repository.CustomerDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerDetailBO {

	@Autowired
	private CustomerDetailRepository customerDetailRepository;

	@Cacheable(cacheNames = "customers")
	public Customer getCustomerDetail(Integer customerId) {
		return customerDetailRepository.getCustomerDetail(customerId);
	}

	@Cacheable(cacheNames = "customerOrders", cacheManager = "alternateCacheManager")
	public List<Order> getCustomerOrders(Integer customerId) {
		return customerDetailRepository.getCustomerOrders(customerId);
	}
}
