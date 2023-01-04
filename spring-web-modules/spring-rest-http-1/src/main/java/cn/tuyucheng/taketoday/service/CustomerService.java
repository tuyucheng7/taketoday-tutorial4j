package cn.tuyucheng.taketoday.service;

import cn.tuyucheng.taketoday.model.Customer;

import java.util.Optional;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Optional<Customer> findCustomer(String id);

    void updateCustomer(Customer customer);
}