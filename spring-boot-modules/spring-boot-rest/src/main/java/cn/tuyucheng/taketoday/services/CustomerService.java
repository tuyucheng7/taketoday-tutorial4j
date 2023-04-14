package cn.tuyucheng.taketoday.services;

import cn.tuyucheng.taketoday.persistence.model.Customer;

import java.util.List;

public interface CustomerService {

	List<Customer> allCustomers();

	Customer getCustomerDetail(final String id);

}
