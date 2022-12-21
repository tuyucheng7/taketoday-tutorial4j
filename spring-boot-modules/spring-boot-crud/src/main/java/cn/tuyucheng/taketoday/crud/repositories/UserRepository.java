package cn.tuyucheng.taketoday.crud.repositories;

import cn.tuyucheng.taketoday.crud.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByName(String name);
}