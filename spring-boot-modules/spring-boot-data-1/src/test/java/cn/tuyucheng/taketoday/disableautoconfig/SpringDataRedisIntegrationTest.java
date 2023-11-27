package cn.tuyucheng.taketoday.disableautoconfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataRedis.class)
class SpringDataRedisIntegrationTest {

   @Autowired
   private ApplicationContext context;

   @Test
   void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
      assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(RedisTemplate.class));
   }
}