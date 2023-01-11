package cn.tuyucheng.taketoday.app.repository;

import cn.tuyucheng.taketoday.app.entity.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {

}
