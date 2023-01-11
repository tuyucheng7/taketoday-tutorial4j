package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(final String username);

}
