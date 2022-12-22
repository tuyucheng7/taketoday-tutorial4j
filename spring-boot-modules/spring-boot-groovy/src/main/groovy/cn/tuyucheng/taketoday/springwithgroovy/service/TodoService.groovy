package cn.tuyucheng.taketoday.springwithgroovy.service

import cn.tuyucheng.taketoday.springwithgroovy.entity.Todo

interface TodoService {

	List<Todo> findAll()

	Todo findById(Integer todoId)

	Todo saveTodo(Todo todo)

	Todo updateTodo(Todo todo)

	Todo deleteTodo(Integer todoId)
}