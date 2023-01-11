package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.tuyucheng.taketoday.boot.domain.Customer;

@Repository
@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Long> {

}