package cn.tuyucheng.taketoday.caching.allkeys;

import cn.tuyucheng.taketoday.caching.allkeys.config.AllKeysConfig;
import cn.tuyucheng.taketoday.caching.allkeys.service.SlowServiceWithCache;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = AllKeysConfig.class)
class GetAllCacheKeysIntegrationTest {

   @Autowired
   private SlowServiceWithCache slowServiceWithCache;
   @Autowired
   private CacheManager cacheManager;

   @Test
   void givenCaffeineCacheCachingSlowCalls_whenCacheManagerProperlyCasted_thenAllKeysAreAccessible() {
      slowServiceWithCache.save("first", "some-value-first");
      slowServiceWithCache.save("second", "other-value-second");

      Cache<Object, Object> caffeine = getNativeCaffeineCacheForSlowService();

      assertThat(caffeine.asMap().keySet()).containsOnly("first", "second");
   }

   private Cache<Object, Object> getNativeCaffeineCacheForSlowService() {
      CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;
      CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache("slowServiceCache");
      return cache.getNativeCache();
   }
}