package cn.tuyucheng.taketoday.boot.embeddedRedis.domain;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash("user")
@ExcludeFromJacocoGeneratedReport
public class User {
	@Id
	private UUID id;
	private String name;

	public User(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}