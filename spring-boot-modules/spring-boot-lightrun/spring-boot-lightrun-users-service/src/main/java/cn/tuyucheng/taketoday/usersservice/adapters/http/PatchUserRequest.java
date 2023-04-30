package cn.tuyucheng.taketoday.usersservice.adapters.http;

import java.util.Optional;

public record PatchUserRequest(Optional<String> name) {
}