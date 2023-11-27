package cn.tuyucheng.taketoday.caching.test;

import cn.tuyucheng.taketoday.caching.config.CachingConfig;
import cn.tuyucheng.taketoday.caching.example.Customer;
import cn.tuyucheng.taketoday.caching.example.CustomerDataService;
import cn.tuyucheng.taketoday.caching.example.CustomerServiceWithParent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CachingConfig.class}, loader = AnnotationConfigContextLoader.class)
class SpringCachingIntegrationTest {

   @Autowired
   private CustomerDataService service;

   @Autowired
   private CustomerServiceWithParent serviceWithParent;

   @Test
   void whenGettingAddress_thenCorrect() {
      final Customer cust = new Customer("Tom", "67-2, Downing Street, NY");
      service.getAddress(cust);
      service.getAddress(cust);

      service.getAddress1(cust);
      service.getAddress1(cust);

      service.getAddress2(cust);
      service.getAddress2(cust);

      service.getAddress3(cust);
      service.getAddress3(cust);

      service.getAddress4(cust);
      service.getAddress4(cust);

      service.getAddress5(cust);
      service.getAddress5(cust);
   }

   @Test
   void givenUsingServiceWithParent_whenGettingAddress_thenCorrect() {
      final Customer cust = new Customer("Tom", "67-2, Downing Street, NY");

      serviceWithParent.getAddress(cust);
      serviceWithParent.getAddress(cust);

      serviceWithParent.getAddress1(cust);
      serviceWithParent.getAddress1(cust);

      serviceWithParent.getAddress2(cust);
      serviceWithParent.getAddress2(cust);

      serviceWithParent.getAddress3(cust);
      serviceWithParent.getAddress3(cust);

      // serviceWithParent.getAddress4(cust);
      // serviceWithParent.getAddress4(cust);

      serviceWithParent.getAddress5(cust);
      serviceWithParent.getAddress5(cust);
   }
}