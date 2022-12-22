package cn.tuyucheng.taketoday.springwithgroovy.repository

import cn.tuyucheng.taketoday.springwithgroovy.entity.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository extends JpaRepository<Todo, Integer> {}