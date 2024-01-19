package cn.tuyucheng.taketoday.spring.cloud.aws.sqs;

public record UserCreatedEvent(String id, String username, String email) {
}