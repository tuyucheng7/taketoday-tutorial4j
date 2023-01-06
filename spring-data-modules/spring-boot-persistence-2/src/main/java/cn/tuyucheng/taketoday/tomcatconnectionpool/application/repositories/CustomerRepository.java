package cn.tuyucheng.taketoday.tomcatconnectionpool.application.repositories;

import cn.tuyucheng.taketoday.tomcatconnectionpool.application.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

	List<Customer> findByLastName(String lastName);
}