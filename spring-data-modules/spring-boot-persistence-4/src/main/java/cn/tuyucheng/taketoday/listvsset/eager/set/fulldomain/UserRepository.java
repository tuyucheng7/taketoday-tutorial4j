package cn.tuyucheng.taketoday.listvsset.eager.set.fulldomain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}