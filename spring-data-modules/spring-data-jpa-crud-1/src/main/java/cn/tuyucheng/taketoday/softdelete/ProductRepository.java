package cn.tuyucheng.taketoday.softdelete;

import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}