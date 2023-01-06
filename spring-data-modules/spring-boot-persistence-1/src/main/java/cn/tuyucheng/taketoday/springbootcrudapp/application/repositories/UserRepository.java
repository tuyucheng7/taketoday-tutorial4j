package cn.tuyucheng.taketoday.springbootcrudapp.application.repositories;

import cn.tuyucheng.taketoday.springbootcrudapp.application.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	List<User> findByName(String name);
}