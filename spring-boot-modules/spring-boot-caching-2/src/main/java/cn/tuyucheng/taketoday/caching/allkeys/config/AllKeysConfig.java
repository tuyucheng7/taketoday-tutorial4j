package cn.tuyucheng.taketoday.caching.allkeys.config;

import cn.tuyucheng.taketoday.caching.allkeys.service.SlowServiceWithCache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AllKeysConfig {

   @Bean
   SlowServiceWithCache slowServiceWithCache() {
      return new SlowServiceWithCache();
   }

   @Bean
   CacheManager cacheManager() {
      return new CaffeineCacheManager();
   }
}