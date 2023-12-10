package cn.tuyucheng.taketoday.entitydtodifferences.repository;

import cn.tuyucheng.taketoday.entitydtodifferences.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}