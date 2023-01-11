package cn.tuyucheng.taketoday.springsecuredsockets.repositories;


import cn.tuyucheng.taketoday.springsecuredsockets.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
