package cn.tuyucheng.taketoday.inmemory.persistence.dao;

import cn.tuyucheng.taketoday.inmemory.persistence.model.ManyTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManyTagRepository extends JpaRepository<ManyTag, Long> {
}