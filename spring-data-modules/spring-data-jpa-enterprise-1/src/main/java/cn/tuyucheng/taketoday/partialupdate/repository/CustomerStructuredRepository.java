package cn.tuyucheng.taketoday.partialupdate.repository;

import cn.tuyucheng.taketoday.partialupdate.model.CustomerStructured;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerStructuredRepository extends CrudRepository<CustomerStructured, Long> {
    CustomerStructured findById(long id);
}