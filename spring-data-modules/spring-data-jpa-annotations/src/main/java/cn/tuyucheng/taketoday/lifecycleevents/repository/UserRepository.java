package cn.tuyucheng.taketoday.lifecycleevents.repository;

import cn.tuyucheng.taketoday.lifecycleevents.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUserName(String userName);
}