package cn.tuyucheng.taketoday.springbootmvc.jsfapplication.model;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TodoDao implements Dao<Todo> {

   private List<Todo> todoList = new ArrayList<>();

   @Override
   public Optional<Todo> get(int id) {
      return Optional.ofNullable(todoList.get(id));
   }

   @Override
   public Collection<Todo> getAll() {
      return todoList.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
   }

   @Override
   public int save(Todo todo) {
      todoList.add(todo);
      int index = todoList.size() - 1;
      todo.setId(index);
      return index;
   }

   @Override
   public void update(Todo todo) {
      todoList.set(todo.getId(), todo);
   }

   @Override
   public void delete(Todo todo) {
      todoList.set(todo.getId(), null);
   }
}