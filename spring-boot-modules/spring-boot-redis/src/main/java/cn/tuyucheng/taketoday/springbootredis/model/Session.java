package cn.tuyucheng.taketoday.springbootredis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@RedisHash(timeToLive = 60L)
public class Session {
   @Id
   private String id;
   @TimeToLive
   private Long expirationInSeconds;
}