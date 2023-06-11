package cn.tuyucheng.taketoday.swagger2boot.repository;

import cn.tuyucheng.taketoday.swagger2boot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}