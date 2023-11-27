package cn.tuyucheng.taketoday.caching.test;

import cn.tuyucheng.taketoday.caching.eviction.service.CachingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class CacheEvictAnnotationIntegrationTest {

   @Configuration
   @EnableCaching
   static class ContextConfiguration {

      @Bean
      public CachingService cachingService() {
         return new CachingService();
      }

      @Bean
      public CacheManager cacheManager() {
         SimpleCacheManager cacheManager = new SimpleCacheManager();
         List<Cache> caches = new ArrayList<>();
         caches.add(cacheBean().getObject());
         cacheManager.setCaches(caches);
         return cacheManager;
      }

      @Bean
      public ConcurrentMapCacheFactoryBean cacheBean() {
         ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
         cacheFactoryBean.setName("first");
         return cacheFactoryBean;
      }
   }

   @Autowired
   CachingService cachingService;

   @BeforeEach
   void before() {
      cachingService.putToCache("first", "key1", "Tuyucheng");
      cachingService.putToCache("first", "key2", "Article");
   }

   @Test
   void givenFirstCache_whenSingleCacheValueEvictRequested_thenEmptyCacheValue() {
      cachingService.evictSingleCacheValue("key1");
      String key1 = cachingService.getFromCache("first", "key1");
      assertThat(key1, is(nullValue()));
   }

   @Test
   void givenFirstCache_whenAllCacheValueEvictRequested_thenEmptyCache() {
      cachingService.evictAllCacheValues();
      String key1 = cachingService.getFromCache("first", "key1");
      String key2 = cachingService.getFromCache("first", "key2");
      assertThat(key1, is(nullValue()));
      assertThat(key2, is(nullValue()));
   }
}