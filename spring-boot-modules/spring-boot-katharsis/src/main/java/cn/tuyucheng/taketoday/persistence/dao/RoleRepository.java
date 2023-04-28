package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
