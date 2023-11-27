package cn.tuyucheng.taketoday.caching.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@EnableCaching
@Configuration
public class ApplicationCacheConfig {

   @Bean
   public CacheManager cacheManager() {
      SimpleCacheManager cacheManager = new SimpleCacheManager();
      Cache booksCache = new ConcurrentMapCache("books");
      cacheManager.setCaches(List.of(booksCache));
      return cacheManager;
   }

   @Bean("customKeyGenerator")
   public KeyGenerator keyGenerator() {
      return new CustomKeyGenerator();
   }
}