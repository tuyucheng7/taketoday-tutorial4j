package cn.tuyucheng.taketoday.springdatawebsupport.application.repositories;

import cn.tuyucheng.taketoday.springdatawebsupport.application.entities.User;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, QuerydslPredicateExecutor<User> {

}