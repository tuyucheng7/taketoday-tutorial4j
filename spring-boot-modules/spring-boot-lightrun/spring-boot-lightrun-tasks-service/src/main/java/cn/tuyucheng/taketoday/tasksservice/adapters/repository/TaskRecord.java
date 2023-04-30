package cn.tuyucheng.taketoday.tasksservice.adapters.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tasks")
public class TaskRecord {
	@Id
	@Column(name = "task_id")
	private String id;
	private String title;
	@Column(name = "created_at")
	private Instant created;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "assigned_to")
	private String assignedTo;
	private String status;

	public TaskRecord(final String id, final String title, final Instant created, final String createdBy, final String assignedTo, final String status) {
		this.id = id;
		this.title = title;
		this.created = created;
		this.createdBy = createdBy;
		this.assignedTo = assignedTo;
		this.status = status;
	}

	private TaskRecord() {
		// Needed for JPA
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Instant getCreated() {
		return created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public String getStatus() {
		return status;
	}

	public void setAssignedTo(final String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public void setStatus(final String status) {
		this.status = status;
	}
}