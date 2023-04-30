package cn.tuyucheng.taketoday.tasksservice.service;

import cn.tuyucheng.taketoday.tasksservice.adapters.repository.TaskRecord;
import cn.tuyucheng.taketoday.tasksservice.adapters.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TasksService {
	@Autowired
	private TasksRepository tasksRepository;

	@Cacheable("tasks")
	public TaskRecord getTaskById(String id) {
		return tasksRepository.findById(id)
			.orElseThrow(() -> new UnknownTaskException(id));
	}

	@Transactional
	public void deleteTaskById(String id) {
		var task = tasksRepository.findById(id)
			.orElseThrow(() -> new UnknownTaskException(id));
		tasksRepository.delete(task);
	}

	public List<TaskRecord> search(Optional<String> createdBy, Optional<String> status) {
		if (createdBy.isPresent() && status.isPresent()) {
			return tasksRepository.findByStatusAndCreatedBy(status.get(), createdBy.get());
		} else if (createdBy.isPresent()) {
			return tasksRepository.findByCreatedBy(createdBy.get());
		} else if (status.isPresent()) {
			return tasksRepository.findByStatus(status.get());
		} else {
			return tasksRepository.findAll();
		}
	}

	@Transactional
	public TaskRecord updateTask(String id, Optional<String> newStatus, Optional<String> newAssignedTo) {
		var task = tasksRepository.findById(id)
			.orElseThrow(() -> new UnknownTaskException(id));

		newStatus.ifPresent(task::setStatus);
		newAssignedTo.ifPresent(task::setAssignedTo);

		return task;
	}

	public TaskRecord createTask(String title, String createdBy) {
		var task = new TaskRecord(UUID.randomUUID()
			.toString(), title, Instant.now(), createdBy, null, "PENDING");
		tasksRepository.save(task);
		return task;
	}
}