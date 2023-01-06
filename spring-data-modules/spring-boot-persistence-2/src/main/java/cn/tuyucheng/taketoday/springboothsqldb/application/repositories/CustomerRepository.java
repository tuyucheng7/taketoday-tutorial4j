package cn.tuyucheng.taketoday.springboothsqldb.application.repositories;

import cn.tuyucheng.taketoday.springboothsqldb.application.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}