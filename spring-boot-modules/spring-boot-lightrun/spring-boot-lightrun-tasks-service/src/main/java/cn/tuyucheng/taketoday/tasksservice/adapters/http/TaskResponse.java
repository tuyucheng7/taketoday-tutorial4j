package cn.tuyucheng.taketoday.tasksservice.adapters.http;

import java.time.Instant;

public record TaskResponse(String id, String title, Instant created, String createdBy, String assignedTo,
						   String status) {
}