package cn.tuyucheng.taketoday.boot.bootstrapmode.repository;

import cn.tuyucheng.taketoday.boot.bootstrapmode.domain.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
}
