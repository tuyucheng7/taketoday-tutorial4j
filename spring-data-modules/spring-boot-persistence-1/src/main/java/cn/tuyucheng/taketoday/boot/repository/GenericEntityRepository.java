package cn.tuyucheng.taketoday.boot.repository;

import cn.tuyucheng.taketoday.boot.domain.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericEntityRepository extends JpaRepository<GenericEntity, Long> {
}