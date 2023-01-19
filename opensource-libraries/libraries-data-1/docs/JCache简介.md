## 1. 概述

简单地说，[JCache](https://www.javadoc.io/doc/javax.cache/cache-api/1.0.0)是Java的标准缓存 API。在本教程中，我们将了解 JCache 是什么以及我们如何使用它。

## 2.Maven依赖

要使用 JCache，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
    <version>1.1.1</version>
</dependency>
```

请注意，我们可以在[Maven Central Repository](https://search.maven.org/classic/#search|ga|1|a%3A"cache-api")中找到最新版本的库。

我们还需要将 API 的实现添加到我们的pom.xml中；我们将在这里使用 Hazelcast：

```xml
<dependency>
    <groupId>com.hazelcast</groupId>
    <artifactId>hazelcast</artifactId>
    <version>5.2.0</version>
</dependency>
```

我们还可以在其[Maven Central Repository](https://search.maven.org/classic/#search|ga|1|a%3A"hazelcast")中找到最新版本的 Hazelcast 。

## 3. JCache 实现

JCache 由各种缓存解决方案实现：

-   JCache 参考实现
-   淡褐色
-   甲骨文一致性
-   陶器Ehcache
-   无穷大

请注意，与其他参考实现不同，不建议在生产中使用 JCache 参考实现，因为它会导致一些并发问题。

## 四、主要部件

### 4.1. 缓存

Cache接口有以下有用的方法：

-   get() – 将元素的键作为参数并返回元素的值；如果缓存中不存在该键，则返回null
-   getAll() – 可以将多个键作为Set 传递给此方法；该方法将给定的键和关联值作为Map返回
-   getAndRemove() – 该方法使用其键检索值并从缓存中删除该元素
-   put() – 在缓存中插入一个新项
-   clear() – 删除缓存中的所有元素
-   containsKey() – 检查缓存是否包含特定键

正如我们所见，这些方法的名称几乎是不言自明的。有关这些方法和其他方法的更多信息，请访问[Javadoc](https://www.javadoc.io/doc/javax.cache/cache-api/1.0.0)。

### 4.2. 缓存管理器

CacheManager是 API 最重要的接口之一。它使我们能够建立、配置和关闭缓存。

### 4.3. 缓存提供者

CachingProvider是一个接口，它允许我们创建和管理CacheManagers的生命周期。

### 4.4. 配置

Configuration是一个接口，使我们能够配置Caches。它有一个具体的实现——MutableConfiguration和一个子接口——CompleteConfiguration。

## 5. 创建缓存

让我们看看如何创建一个简单的缓存：

```java
CachingProvider cachingProvider = Caching.getCachingProvider();
CacheManager cacheManager = cachingProvider.getCacheManager();
MutableConfiguration<String, String> config
  = new MutableConfiguration<>();
Cache<String, String> cache = cacheManager
  .createCache("simpleCache", config);
cache.put("key1", "value1");
cache.put("key2", "value2");
cacheManager.close();
```

我们所做的只是：

-   创建一个CachingProvider对象，我们用它来构造CacheManager对象
-   创建一个MutableConfiguration对象，它是Configuration接口的实现
-   使用我们之前创建的CacheManager对象创建Cache对象
-   把所有的条目，我们需要缓存到我们的缓存对象中
-   关闭CacheManager以释放Cache使用的资源

如果我们不在pom.xml中提供 JCache 的任何实现，将抛出以下异常：

```java
javax.cache.CacheException: No CachingProviders have been configured
```

原因是 JVM 找不到getCacheManager()方法的任何具体实现。

## 6.入口处理器

EntryProcessor允许我们使用原子操作修改Cache条目，而不必将它们重新添加到Cache中。要使用它，我们需要实现EntryProcessor接口：

```java
public class SimpleEntryProcessor
  implements EntryProcessor<String, String, String>, Serializable {
    
    public String process(MutableEntry<String, String> entry, Object... args)
      throws EntryProcessorException {

        if (entry.exists()) {
            String current = entry.getValue();
            entry.setValue(current + " - modified");
            return current;
        }
        return null;
    }
}
```

现在，让我们使用我们的EntryProcessor实现：

```java
@Test
public void whenModifyValue_thenCorrect() {
    this.cache.invoke("key", new SimpleEntryProcessor());
 
    assertEquals("value - modified", cache.get("key"));
}
```

## 7.事件监听器

事件监听器允许我们在触发EventType枚举中定义的任何事件类型时采取行动，它们是：

-   已创建
-   更新
-   移除
-   已到期

首先，我们需要实现我们将要使用的事件的接口。

例如，如果我们想使用CREATED和UPDATED事件类型，那么我们应该实现CacheEntryCreatedListener和CacheEntryUpdatedListener接口。

让我们看一个例子：

```java
public class SimpleCacheEntryListener implements
  CacheEntryCreatedListener<String, String>,
  CacheEntryUpdatedListener<String, String>,
  Serializable {
    
    private boolean updated;
    private boolean created;
    
    // standard getters
    
    public void onUpdated(
      Iterable<CacheEntryEvent<? extends String,
      ? extends String>> events) throws CacheEntryListenerException {
        this.updated = true;
    }
    
    public void onCreated(
      Iterable<CacheEntryEvent<? extends String,
      ? extends String>> events) throws CacheEntryListenerException {
        this.created = true;
    }
}
```

现在，让我们运行我们的测试：

```java
@Test
public void whenRunEvent_thenCorrect() throws InterruptedException {
    this.listenerConfiguration
      = new MutableCacheEntryListenerConfiguration<String, String>(
        FactoryBuilder.factoryOf(this.listener), null, false, true);
    this.cache.registerCacheEntryListener(this.listenerConfiguration);
    
    assertEquals(false, this.listener.getCreated());
    
    this.cache.put("key", "value");
 
    assertEquals(true, this.listener.getCreated());
    assertEquals(false, this.listener.getUpdated());
    
    this.cache.put("key", "newValue");
 
    assertEquals(true, this.listener.getUpdated());
}
```

## 8.缓存加载器

CacheLoader允许 我们使用 read-through 模式 将缓存作为主要的数据存储并从中读取数据。

在真实场景中，我们可以让缓存从实际存储中读取数据。

让我们看一个例子。首先，我们应该实现CacheLoader接口：

```java
public class SimpleCacheLoader
  implements CacheLoader<Integer, String> {

    public String load(Integer key) throws CacheLoaderException {
        return "fromCache" + key;
    }
    
    public Map<Integer, String> loadAll(Iterable<? extends Integer> keys)
      throws CacheLoaderException {
        Map<Integer, String> data = new HashMap<>();
        for (int key : keys) {
            data.put(key, load(key));
        }
        return data;
    }
}
```

现在，让我们使用我们的CacheLoader实现：

```java
public class CacheLoaderIntegrationTest {
    
    private Cache<Integer, String> cache;
    
    @Before
    public void setup() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<Integer, String> config
          = new MutableConfiguration<>()
            .setReadThrough(true)
            .setCacheLoaderFactory(new FactoryBuilder.SingletonFactory<>(
              new SimpleCacheLoader()));
        this.cache = cacheManager.createCache("SimpleCache", config);
    }
    
    @Test
    public void whenReadingFromStorage_thenCorrect() {
        for (int i = 1; i < 4; i++) {
            String value = cache.get(i);
 
            assertEquals("fromCache" + i, value);
        }
    }
}
```

## 9.总结

在本教程中，我们了解了 JCache 是什么，并在一些实际场景中探索了它的一些重要特性。