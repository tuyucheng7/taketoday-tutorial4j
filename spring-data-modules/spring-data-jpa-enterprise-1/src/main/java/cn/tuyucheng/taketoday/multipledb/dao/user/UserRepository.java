package cn.tuyucheng.taketoday.multipledb.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.tuyucheng.taketoday.multipledb.model.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}