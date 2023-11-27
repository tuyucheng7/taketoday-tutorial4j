package cn.tuyucheng.taketoday.caching.ttl.service;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringCacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

   @Override
   public void customize(ConcurrentMapCacheManager cacheManager) {
      cacheManager.setCacheNames(List.of("hotels"));
   }
}