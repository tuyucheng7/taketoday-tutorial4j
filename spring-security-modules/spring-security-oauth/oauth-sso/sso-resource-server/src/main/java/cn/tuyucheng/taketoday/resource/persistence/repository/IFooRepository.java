package cn.tuyucheng.taketoday.resource.persistence.repository;

import cn.tuyucheng.taketoday.resource.persistence.model.Foo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IFooRepository extends PagingAndSortingRepository<Foo, Long> {
}
