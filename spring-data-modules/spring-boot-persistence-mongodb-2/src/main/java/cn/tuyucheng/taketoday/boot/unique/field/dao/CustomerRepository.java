package cn.tuyucheng.taketoday.boot.unique.field.dao;

import cn.tuyucheng.taketoday.boot.unique.field.data.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
	Optional<Customer> findByStoreIdAndNumber(Long storeId, Long number);
}