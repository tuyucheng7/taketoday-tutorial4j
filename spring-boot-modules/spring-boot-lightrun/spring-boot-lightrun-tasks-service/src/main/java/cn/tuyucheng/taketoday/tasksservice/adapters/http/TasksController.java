package cn.tuyucheng.taketoday.tasksservice.adapters.http;

import cn.tuyucheng.taketoday.tasksservice.adapters.repository.TaskRecord;
import cn.tuyucheng.taketoday.tasksservice.service.TasksService;
import cn.tuyucheng.taketoday.tasksservice.service.UnknownTaskException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
class TasksController {
	@Autowired
	private TasksService tasksService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponse createTask(@RequestBody CreateTaskRequest body) {
		var task = tasksService.createTask(body.title(), body.createdBy());
		return buildResponse(task);
	}

	@GetMapping
	public List<TaskResponse> searchTasks(@RequestParam("status") Optional<String> status, @RequestParam("createdBy") Optional<String> createdBy) {
		var tasks = tasksService.search(status, createdBy);

		return tasks.stream()
			.map(this::buildResponse)
			.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public TaskResponse getTask(@PathVariable("id") String id) {
		var task = tasksService.getTaskById(id);
		return buildResponse(task);
	}

	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable("id") String id) {
		tasksService.deleteTaskById(id);
	}

	@PatchMapping("/{id}")
	public TaskResponse patchTask(@PathVariable("id") String id, @RequestBody PatchTaskRequest body) {
		var task = tasksService.updateTask(id, body.status(), body.assignedTo());
		return buildResponse(task);
	}

	private TaskResponse buildResponse(final TaskRecord task) {
		return new TaskResponse(task.getId(), task.getTitle(), task.getCreated(), task.getCreatedBy(), task.getAssignedTo(), task.getStatus());
	}

	@ExceptionHandler(UnknownTaskException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleUnknownTask() {
	}
}