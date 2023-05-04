package cn.tuyucheng.taketoday.examples.r2dbc.flyway.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import cn.tuyucheng.taketoday.examples.r2dbc.flyway.model.Department;

public interface DepartmentRepository extends ReactiveCrudRepository<Department, UUID> {
}