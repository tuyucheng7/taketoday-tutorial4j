package cn.tuyucheng.taketoday.dip.mainapp;

import cn.tuyucheng.taketoday.dip.daoimplementations.SimpleCustomerDao;
import cn.tuyucheng.taketoday.dip.entities.Customer;
import cn.tuyucheng.taketoday.dip.services.CustomerService;

import java.util.HashMap;

public class MainApplication {

	public static void main(String[] args) {
		var customers = new HashMap<Integer, Customer>();
		customers.put(1, new Customer("John"));
		customers.put(2, new Customer("Susan"));
		CustomerService customerService = new CustomerService(new SimpleCustomerDao(customers));
		customerService.findAll().forEach(System.out::println);
	}
}