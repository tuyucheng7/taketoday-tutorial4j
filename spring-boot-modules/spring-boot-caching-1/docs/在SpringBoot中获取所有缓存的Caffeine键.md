## 1. 概述

在本文中，我们将学习如何在与[Spring的Cache抽象层](https://www.baeldung.com/spring-cache-tutorial)一起使用时获取Caffeine缓存中的所有缓存键。

## 2. Spring缓存

缓存是Spring框架不可或缺的一部分。自3.1版本以来，它一直是Spring生态系统的一部分。因此，它具有一组定义明确且经过实战检验的接口。

让我们来看看两个主要的：CacheManager和Cache：

```java
interface CacheManager {
    Cache getCache(String name);

    Collection<String> getCacheNames();
}

public interface Cache {
    String getName();

    Object getNativeCache();

    ValueWrapper get(Object key);

    <T> T get(Object key, @Nullable Class<T> type);

    <T> T get(Object key, Callable<T> valueLoader);

    void put(Object key, @Nullable Object value);

    ValueWrapper putIfAbsent(Object key, @Nullable Object value);

    void evict(Object key);

    void clear();
}
```

正如我们所看到的，CacheManager只是一个包装器。应用程序中可用的缓存区域注册表。另一方面，Cache对象是区域内的一组键值对。

**但是，它们都没有提供列出可用键的方法**。

## 3. 设置

在我们探索访问所有可用键集合的选项之前，让我们定义测试应用程序使用的CaffeineCacheManager：

```java
@Configuration
@EnableCaching
public class AllKeysConfig {

    @Bean
    CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
}
```

然后，让我们创建一个慢速服务，它会在每次调用时填充缓存：

```java
public class SlowServiceWithCache {

    @CachePut(cacheNames = "slowServiceCache", key = "#name")
    public String save(String name, String details) {
        return details;
    }
}
```

有了管理器和服务，我们就可以在slowServiceCache区域中查找键了。

## 4. 访问所有缓存键

正如我们已经了解到的，CacheManager没有公开任何方法来访问所有可用的键。Cache接口也没有。

**因此，我们需要使用我们在应用程序中定义的实际缓存实现的知识**。让我们将Spring的通用接口转换为其适当的Caffeine实现。

我们需要先注入CacheManager：

```java
@Autowired
CacheManager cacheManager;
```

然后让我们做一些简单的强制转换操作来访问原生的Caffeine Cache：

```java
CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;
CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache("slowServiceCache");
Cache<Object, Object> caffeine = cache.getNativeCache();
```

然后，让我们调用caffeine.asMap()。因为它是一个Map，我们可以简单地通过调用caffeine.asMap().keySet()来访问键：

```java
@Test
public void givenCaffeineCacheCachingSlowCalls_whenCacheManagerProperlyCasted_thenAllKeysAreAccessible() {
    slowServiceWithCache.save("first", "some-value-first");
    slowServiceWithCache.save("second", "other-value-second");

    Cache<Object, Object> caffeine = getNativeCaffeineCacheForSlowService();

    assertThat(caffeine.asMap().keySet()).containsOnly("first", "second");
}
```

## 4. 总结

在本文中，我们学习了如何从与Spring Cache一起使用的Caffeine缓存中访问所有可用键的集合。了解我们正在处理的实际缓存后，访问所有可用键只需要几个简单的强制转换操作。