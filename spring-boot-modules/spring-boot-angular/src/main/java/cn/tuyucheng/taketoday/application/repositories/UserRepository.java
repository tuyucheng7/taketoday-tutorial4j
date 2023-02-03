package cn.tuyucheng.taketoday.application.repositories;

import cn.tuyucheng.taketoday.application.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}