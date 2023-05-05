package cn.tuyucheng.taketoday.spring.data.jpa.paging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

   public CustomerService(CustomerRepository customerRepository) {
      this.customerRepository = customerRepository;
   }

   private final CustomerRepository customerRepository;

   public Page<Customer> getCustomers(int page, int size) {
      Pageable pageRequest = createPageRequestUsing(page, size);

      List<Customer> allCustomers = customerRepository.findAll();
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), allCustomers.size());

      return new PageImpl<>(allCustomers.subList(start, end), pageRequest, allCustomers.size());
   }

   private Pageable createPageRequestUsing(int page, int size) {
      return PageRequest.of(page, size);
   }
}