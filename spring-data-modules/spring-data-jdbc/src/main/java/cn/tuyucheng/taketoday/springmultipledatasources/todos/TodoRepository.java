package cn.tuyucheng.taketoday.springmultipledatasources.todos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}