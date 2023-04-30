package cn.tuyucheng.taketoday.tasksservice.adapters.http;

import java.util.Optional;

public record PatchTaskRequest(Optional<String> status, Optional<String> assignedTo) {
}