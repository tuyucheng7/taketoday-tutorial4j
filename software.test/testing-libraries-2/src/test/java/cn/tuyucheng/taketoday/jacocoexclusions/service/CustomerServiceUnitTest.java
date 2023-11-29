package cn.tuyucheng.taketoday.jacocoexclusions.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerServiceUnitTest {

   @Test
   void givenCustomer_whenGetCustomer_thenReturnNewCustomer() {
      CustomerService customerService = new CustomerService();
      assertNotNull(customerService.getCustomerName());
   }
}