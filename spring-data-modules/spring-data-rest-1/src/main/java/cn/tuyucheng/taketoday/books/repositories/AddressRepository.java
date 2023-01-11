package cn.tuyucheng.taketoday.books.repositories;

import cn.tuyucheng.taketoday.books.models.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {

}