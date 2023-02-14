package com.baeldung.boot.bootstrapmode.repository;

import com.baeldung.boot.bootstrapmode.domain.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
}
