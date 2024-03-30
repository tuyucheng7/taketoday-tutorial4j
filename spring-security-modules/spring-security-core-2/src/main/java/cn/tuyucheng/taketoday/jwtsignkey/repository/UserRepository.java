package cn.tuyucheng.taketoday.jwtsignkey.repository;

import cn.tuyucheng.taketoday.jwtsignkey.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

   Boolean existsByUsername(String username);

   Optional<User> findByUsername(String username);

}
