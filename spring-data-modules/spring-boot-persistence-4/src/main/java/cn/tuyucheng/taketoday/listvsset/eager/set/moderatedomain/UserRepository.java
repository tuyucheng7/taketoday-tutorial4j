package cn.tuyucheng.taketoday.listvsset.eager.set.moderatedomain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}