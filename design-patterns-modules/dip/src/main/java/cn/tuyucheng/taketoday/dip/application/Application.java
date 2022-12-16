package cn.tuyucheng.taketoday.dip.application;

import cn.tuyucheng.taketoday.dip.daoimplementations.SimpleCustomerDao;
import cn.tuyucheng.taketoday.dip.entities.Customer;
import cn.tuyucheng.taketoday.dip.services.CustomerService;
import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.HashMap;
import java.util.Map;

@ExcludeFromJacocoGeneratedReport
public class Application {

	public static void main(String[] args) {
		Map<Integer, Customer> customers = new HashMap<>();
		customers.put(1, new Customer("John"));
		customers.put(2, new Customer("Susan"));
		CustomerService customerService = new CustomerService(new SimpleCustomerDao(customers));
		customerService.findAll().forEach(System.out::println);
	}
}