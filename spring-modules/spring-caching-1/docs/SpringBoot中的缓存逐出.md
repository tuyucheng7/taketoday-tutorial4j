## 1. 概述

在这个简短的教程中，我们学习**如何使用Spring执行缓存逐出**。

在这之前，请阅读另一篇文章 - [Guide To Caching in Spring](Spring缓存指南.md)，以了解Spring缓存的工作原理。

## 2. 如何逐出缓存？

Spring提供了两种逐出缓存的方法，一种是在方法上使用@CacheEvict注解，另一种是自动注入CacheManger并通过调用clear()方法清除它。

### 2.1 使用@CacheEvict

让我们创建一个带有@CacheEvict注解的空方法，
并提供我们要清除的缓存名称作为注解的参数(在这种情况下，我们要清除名称为“first”的缓存)：

```java

@Component
public class CachingService {

    @CacheEvict(value = "first", allEntries = true)
    public void evictAllCacheValues() {
    }
}
```

**Spring将拦截所有使用@CacheEvict注解标注的方法，并根据allEntries标志清除所有值**。

我们也可以根据特定key逐出值。为此，我们所要做的就是将缓存的key作为参数传递给注解，而不是声明allEntries标志：

```java
public class CachingService {

    @CacheEvict(value = "first", key = "#cacheKey")
    public void evictSingleCacheValue(String cacheKey) {
    }
}
```

由于key属性的值是动态的，我们可以使用Spring表达式语言或自定义key生成器，通过实现KeyGenerator来选择感兴趣的参数或嵌套属性。

### 2.2 使用CacheManager

接下来，让我们看看如何使用Spring Cache模块提供的CacheManager来逐出缓存。

首先，我们必须自动注入已实现的CacheManager bean。然后我们可以根据需要用它清除缓存：

```java

public class CachingService {

    @Autowired
    CacheManager cacheManager;

    public void evictSingleCacheValue(String cacheName, String cacheKey) {
        cacheManager.getCache(cacheName).evict(cacheKey);
    }

    public void evictAllCacheValues(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }
}
```

正如我们在代码中看到的，**clear()方法将清除所有缓存条目，而evict()方法基于key清除值**。

## 3. 如何逐出所有缓存？

Spring不提供开箱即用的功能来清除所有缓存，但是我们可以通过使用CacheManager的getCacheNames()方法轻松实现这一点。

### 3.1 按需逐出

现在让我们看看如何按需清除所有缓存。为了提供逐出缓存的触发点，我们必须先暴露一个端点：

```java

@RestController
public class CachingController {

    @Autowired
    CachingService cachingService;

    @GetMapping("clearAllCaches")
    public void clearAllCaches() {
        cachingService.evictAllCaches();
    }
}
```

在CachingService中，我们可以**通过迭代从CacheManager获得的缓存名称来清除所有缓存**：

```java
public class CachingService {
    @Autowired
    CacheManager cacheManager;

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
}
```

### 3.2 自动逐出

**在某些用例中，应该以特定时间间隔自动执行缓存逐出，在这种情况下，我们可以使用Spring的任务调度器**：

```java
public class CachingService {

    @Scheduled(fixedRate = 6000)
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }
}
```

## 4. 总结

我们介绍了如何以不同的方式逐出缓存。关于这些机制值得注意的一点是，它可以与所有不同的缓存实现一起使用，如eh-cache、infini-span、apache-ignite等。