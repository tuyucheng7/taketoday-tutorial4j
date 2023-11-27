package cn.tuyucheng.taketoday.caching.boot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest("spring.cache.type=simple")
class SimpleCacheCustomizerIntegrationTest {

   @Autowired
   private CacheManager cacheManager;

   @Test
   void givenCacheManagerCustomizerWhenBootstrappedThenCacheManagerCustomized() {
      assertThat(cacheManager.getCacheNames())
            .containsOnly(SimpleCacheCustomizer.USERS_CACHE, SimpleCacheCustomizer.TRANSACTIONS_CACHE);
   }
}