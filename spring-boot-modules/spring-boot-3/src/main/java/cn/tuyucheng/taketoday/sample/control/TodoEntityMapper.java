package cn.tuyucheng.taketoday.sample.control;

import cn.tuyucheng.taketoday.sample.entity.TodoEntity;
import org.mapstruct.Mapper;

/**
 * Dieser Mapper kopiert die Informationen zwischen den Schichten.
 */
@Mapper(componentModel = "spring")
interface TodoEntityMapper {

	TodoEntity map(Todo todo);

	Todo map(TodoEntity todo);
}