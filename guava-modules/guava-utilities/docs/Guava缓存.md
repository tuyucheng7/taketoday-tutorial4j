## 1. 概述

在本教程中，我们将重点介绍Guava Cache的实现，包括基本用法、驱逐策略、刷新缓存以及一些有趣的批量操作。

最后，我们将讨论如何使用缓存能够发送的删除通知。

## 2. Guava缓存的使用方法

让我们从缓存String实例的大写形式的简单示例开始。

首先，我们将创建CacheLoader，用于计算存储在缓存中的值。由此，我们将使用方便的CacheBuilder来使用给定的规范构建我们的缓存：

```java
@Test
public void whenCacheMiss_thenValueIsComputed() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder().build(loader);

    assertEquals(0, cache.size());
    assertEquals("HELLO", cache.getUnchecked("hello"));
    assertEquals(1, cache.size());
}
```

请注意我们的“hello”键在缓存中没有值，因此计算并缓存了该值。

另请注意，我们正在使用getUnchecked()操作，如果值不存在，它会计算值并将其加载到缓存中。

## 3. 驱逐政策

每个缓存都需要在某个时候删除值。让我们讨论使用不同标准将值逐出缓存的机制。

### 3.1. 按大小驱逐

我们可以使用maximumSize()来限制缓存的大小。如果缓存达到限制，它会驱逐最旧的项目。

在下面的代码中，我们将缓存大小限制为三个记录：

```java
@Test
public void whenCacheReachMaxSize_thenEviction() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };
    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder().maximumSize(3).build(loader);

    cache.getUnchecked("first");
    cache.getUnchecked("second");
    cache.getUnchecked("third");
    cache.getUnchecked("forth");
    assertEquals(3, cache.size());
    assertNull(cache.getIfPresent("first"));
    assertEquals("FORTH", cache.getIfPresent("forth"));
}
```

### 3.2. 按重量驱逐

我们还可以使用自定义权重函数来限制缓存大小。在下面的代码中，我们将使用长度作为我们的自定义权重函数：

```java
@Test
public void whenCacheReachMaxWeight_thenEviction() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    Weigher<String, String> weighByLength;
    weighByLength = new Weigher<String, String>() {
        @Override
        public int weigh(String key, String value) {
            return value.length();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder()
      .maximumWeight(16)
      .weigher(weighByLength)
      .build(loader);

    cache.getUnchecked("first");
    cache.getUnchecked("second");
    cache.getUnchecked("third");
    cache.getUnchecked("last");
    assertEquals(3, cache.size());
    assertNull(cache.getIfPresent("first"));
    assertEquals("LAST", cache.getIfPresent("last"));
}
```

注意：缓存可能会删除多个记录以为新的大记录留出空间。

### 3.3. 按时间驱逐

除了使用大小来驱逐旧记录外，我们还可以使用时间。在下面的示例中，我们将自定义缓存以删除空闲 2ms 的记录：

```java
@Test
public void whenEntryIdle_thenEviction()
  throws InterruptedException {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder()
      .expireAfterAccess(2,TimeUnit.MILLISECONDS)
      .build(loader);

    cache.getUnchecked("hello");
    assertEquals(1, cache.size());

    cache.getUnchecked("hello");
    Thread.sleep(300);

    cache.getUnchecked("test");
    assertEquals(1, cache.size());
    assertNull(cache.getIfPresent("hello"));
}
```

我们还可以根据记录的总生存时间驱逐记录。在下面的示例中，缓存将在记录存储 2 毫秒后将其删除：

```java
@Test
public void whenEntryLiveTimeExpire_thenEviction()
  throws InterruptedException {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder()
      .expireAfterWrite(2,TimeUnit.MILLISECONDS)
      .build(loader);

    cache.getUnchecked("hello");
    assertEquals(1, cache.size());
    Thread.sleep(300);
    cache.getUnchecked("test");
    assertEquals(1, cache.size());
    assertNull(cache.getIfPresent("hello"));
}
```

## 4.弱键

接下来，我们将演示如何使我们的缓存键具有弱引用，从而允许垃圾收集器收集其他地方未引用的缓存键。

默认情况下，缓存键和值都具有强引用，但我们可以通过使用weakKeys()使我们的缓存使用弱引用存储键：

```java
@Test
public void whenWeakKeyHasNoRef_thenRemoveFromCache() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder().weakKeys().build(loader);
}
```

## 5. 软价值观

我们还可以允许垃圾收集器使用softValues()收集我们缓存的值：

```java
@Test
public void whenSoftValue_thenRemoveFromCache() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder().softValues().build(loader);
}
```

注意：太多的软引用可能会影响系统性能，所以首选是使用maximumSize()。

## 6. 处理空值

现在让我们看看如何处理缓存空值。默认情况下，如果我们尝试加载null值， Guava Cache将抛出异常，因为缓存null没有任何意义。

但是如果空值在我们的代码中意味着什么，那么我们可以充分利用Optional类：

```java
@Test
public void whenNullValue_thenOptional() {
    CacheLoader<String, Optional<String>> loader;
    loader = new CacheLoader<String, Optional<String>>() {
        @Override
        public Optional<String> load(String key) {
            return Optional.fromNullable(getSuffix(key));
        }
    };

    LoadingCache<String, Optional<String>> cache;
    cache = CacheBuilder.newBuilder().build(loader);

    assertEquals("txt", cache.getUnchecked("text.txt").get());
    assertFalse(cache.getUnchecked("hello").isPresent());
}
private String getSuffix(final String str) {
    int lastIndex = str.lastIndexOf('.');
    if (lastIndex == -1) {
        return null;
    }
    return str.substring(lastIndex + 1);
}
```

## 7.刷新缓存

接下来，我们将学习如何刷新缓存值。

### 7.1. 手动刷新

我们可以在LoadingCache.refresh(key)的帮助下手动刷新单个键：

```java
String value = loadingCache.get("key");
loadingCache.refresh("key");
```

这将强制CacheLoader加载键的新值。

在成功加载新值之前，键的先前值将由get(key)返回。

### 7.2. 自动刷新

我们还可以使用CacheBuilder.refreshAfterWrite(duration)来自动刷新缓存值：

```java
@Test
public void whenLiveTimeEnd_thenRefresh() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder()
      .refreshAfterWrite(1,TimeUnit.MINUTES)
      .build(loader);
}
```

重要的是要了解refreshAfterWrite(duration)仅使密钥在指定的 duration 后符合刷新条件。只有当通过get(key)查询到相应的条目时，该值才会真正被刷新。

## 8.预加载缓存

我们可以使用putAll()方法在缓存中插入多条记录。在以下示例中，我们将使用Map将多条记录添加到我们的缓存中：

```java
@Test
public void whenPreloadCache_thenUsePutAll() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return key.toUpperCase();
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder().build(loader);

    Map<String, String> map = new HashMap<String, String>();
    map.put("first", "FIRST");
    map.put("second", "SECOND");
    cache.putAll(map);

    assertEquals(2, cache.size());
}
```

## 9.移除通知

有时我们需要在从缓存中删除记录时采取措施，因此我们将讨论RemovalNotification。

我们可以注册一个RemovalListener来获取记录被删除的通知。我们还可以通过getCause()方法访问删除的原因。

在以下示例中，当缓存中的第四个元素因其大小而被删除时，会收到RemovalNotification ：

```java
@Test
public void whenEntryRemovedFromCache_thenNotify() {
    CacheLoader<String, String> loader;
    loader = new CacheLoader<String, String>() {
        @Override
        public String load(final String key) {
            return key.toUpperCase();
        }
    };

    RemovalListener<String, String> listener;
    listener = new RemovalListener<String, String>() {
        @Override
        public void onRemoval(RemovalNotification<String, String> n){
            if (n.wasEvicted()) {
                String cause = n.getCause().name();
                assertEquals(RemovalCause.SIZE.toString(),cause);
            }
        }
    };

    LoadingCache<String, String> cache;
    cache = CacheBuilder.newBuilder()
      .maximumSize(3)
      .removalListener(listener)
      .build(loader);

    cache.getUnchecked("first");
    cache.getUnchecked("second");
    cache.getUnchecked("third");
    cache.getUnchecked("last");
    assertEquals(3, cache.size());
}
```

## 10. 注意事项

最后，这里有一些关于 Guava 缓存实现的附加快速说明：

-   它是线程安全的
-   我们可以使用put(key,value)手动将值插入缓存
-   我们可以使用CacheStats(hitRate() , missRate() , ..)来衡量我们的缓存性能

## 11.总结

在本文中，我们探讨了 Guava Cache 的许多用例。我们讨论的主题包括简单使用、逐出元素、刷新和预加载缓存以及删除通知。