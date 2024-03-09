package cn.tuyucheng.taketoday.skipselectbeforeinsert.repository;

import cn.tuyucheng.taketoday.skipselectbeforeinsert.model.TaskWithGeneratedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskWithGeneratedIdRepository extends JpaRepository<TaskWithGeneratedId, Integer> {
}