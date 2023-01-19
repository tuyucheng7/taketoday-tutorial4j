package cn.tuyucheng.taketoday.libraries.debezium.repository;

import cn.tuyucheng.taketoday.libraries.debezium.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
