package cn.tuyucheng.taketoday.javers.repo;

import cn.tuyucheng.taketoday.javers.domain.Store;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

@JaversSpringDataAuditable
public interface StoreRepository extends CrudRepository<Store, Integer> {
}
