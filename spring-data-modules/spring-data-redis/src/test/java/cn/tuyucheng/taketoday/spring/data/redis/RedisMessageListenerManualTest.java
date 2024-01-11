package cn.tuyucheng.taketoday.spring.data.redis;

import cn.tuyucheng.taketoday.spring.data.redis.queue.RedisMessagePublisher;
import cn.tuyucheng.taketoday.spring.data.redis.queue.RedisMessageSubscriber;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServerBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringRedisApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class RedisMessageListenerManualTest {

   private static redis.embedded.RedisServer redisServer;

   @Autowired
   private RedisMessagePublisher redisMessagePublisher;

   @BeforeAll
   static void startRedisServer() {
      redisServer = new RedisServerBuilder().port(6379).setting("maxmemory 256M").build();
      redisServer.start();
   }

   @AfterAll
   static void stopRedisServer() {
      redisServer.stop();
   }

   @Test
   void testOnMessage() throws Exception {
      String message = "Message " + UUID.randomUUID();
      redisMessagePublisher.publish(message);
      Thread.sleep(1000);
      assertTrue(RedisMessageSubscriber.messageList.get(0).contains(message));
   }
}