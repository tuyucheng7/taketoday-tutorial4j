package cn.tuyucheng.taketoday.restdocopenapi;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FooRepository extends PagingAndSortingRepository<Foo, Long> {

}