package cn.tuyucheng.taketoday.skipselectbeforeinsert.repository;

import cn.tuyucheng.taketoday.skipselectbeforeinsert.model.PersistableTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistableTaskRepository extends JpaRepository<PersistableTask, Integer> {
}