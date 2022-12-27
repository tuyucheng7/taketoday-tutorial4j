package cn.tuyucheng.taketoday.beanvalidation.application.repositories;

import cn.tuyucheng.taketoday.beanvalidation.application.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}