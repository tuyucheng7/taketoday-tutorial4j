package cn.tuyucheng.taketoday.spring.cloud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DataFlowServerApplicationIntegrationTest {

   @Test
   void contextLoads() {
   }

   @EnableRedisHttpSession
   @Configuration
   static class Config {

      @Bean
      @SuppressWarnings("unchecked")
      public RedisSerializer<Object> defaultRedisSerializer() {
         return Mockito.mock(RedisSerializer.class);
      }

      @Bean
      public RedisConnectionFactory connectionFactory() {
         RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
         RedisConnection connection = Mockito.mock(RedisConnection.class);
         Mockito.when(factory.getConnection()).thenReturn(connection);

         return factory;
      }

      @Bean
      public static ConfigureRedisAction configureRedisAction() {
         return ConfigureRedisAction.NO_OP;
      }
   }
}