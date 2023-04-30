package cn.tuyucheng.taketoday.tasksservice.adapters.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<TaskRecord, String> {
	List<TaskRecord> findByStatus(String status);

	List<TaskRecord> findByCreatedBy(String createdBy);

	List<TaskRecord> findByStatusAndCreatedBy(String status, String createdBy);

	List<TaskRecord> findByAssignedTo(String assignedTo);
}