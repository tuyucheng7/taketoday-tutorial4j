package cn.tuyucheng.taketoday.springbootdatasourceconfig.application.repositories;

import cn.tuyucheng.taketoday.springbootdatasourceconfig.application.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}