package cn.tuyucheng.taketoday.spring.data.persistence.findvsget.repository;

import cn.tuyucheng.taketoday.spring.data.persistence.findvsget.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}