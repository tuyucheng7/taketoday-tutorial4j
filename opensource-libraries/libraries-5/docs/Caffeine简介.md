## 1. 简介

在本文中，我们将了解[Caffeine](https://github.com/ben-manes/caffeine) — 一个用于Java的高性能缓存库。

缓存和Map之间的一个根本区别是缓存逐出存储的项目。

逐出策略决定在任何给定时间应删除哪些对象。该策略直接影响缓存的命中率——缓存库的一个重要特征。

Caffeine 使用Window TinyLfu逐出策略，它提供了接近最优的命中率。

## 2. 依赖

我们需要将咖啡因依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>2.5.5</version>
</dependency>
```

[可以在 Maven Central 上](https://search.maven.org/classic/#search|gav|1|g%3A"com.github.ben-manes.caffeine" AND a%3A"caffeine")找到最新版本的咖啡因 。

## 3.填充缓存

下面重点介绍 Caffeine缓存填充的三种策略：手动、同步加载和异步加载。

首先，让我们为将存储在缓存中的值类型编写一个类：

```java
class DataObject {
    private final String data;

    private static int objectCounter = 0;
    // standard constructors/getters
    
    public static DataObject get(String data) {
        objectCounter++;
        return new DataObject(data);
    }
}
```

### 3.1. 手动填充

在此策略中，我们手动将值放入缓存中并稍后检索它们。

让我们初始化我们的缓存：

```java
Cache<String, DataObject> cache = Caffeine.newBuilder()
  .expireAfterWrite(1, TimeUnit.MINUTES)
  .maximumSize(100)
  .build();
```

现在，我们可以使用getIfPresent方法从缓存中获取一些值。如果该值不在缓存中，此方法将返回null ：

```java
String key = "A";
DataObject dataObject = cache.getIfPresent(key);

assertNull(dataObject);
```

我们可以使用put方法手动填充缓存：

```java
cache.put(key, dataObject);
dataObject = cache.getIfPresent(key);

assertNotNull(dataObject);
```

我们还可以使用get方法获取值，该方法将一个函数和一个键作为参数。如果缓存中不存在键，此函数将用于提供回退值，该键将在计算后插入缓存中：

```java
dataObject = cache
  .get(key, k -> DataObject.get("Data for A"));

assertNotNull(dataObject);
assertEquals("Data for A", dataObject.getData());
```

get方法以原子方式执行计算。这意味着计算只会进行一次——即使多个线程同时请求该值。这就是为什么使用get优于getIfPresent的原因。

有时我们需要手动使一些缓存值失效：

```java
cache.invalidate(key);
dataObject = cache.getIfPresent(key);

assertNull(dataObject);
```

### 3.2. 同步加载

这种加载缓存的方法需要一个Function，用于初始化值，类似于手动策略的get方法。让我们看看我们如何使用它。

首先，我们需要初始化缓存：

```java
LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .maximumSize(100)
  .expireAfterWrite(1, TimeUnit.MINUTES)
  .build(k -> DataObject.get("Data for " + k));
```

现在我们可以使用get方法检索值：

```java
DataObject dataObject = cache.get(key);

assertNotNull(dataObject);
assertEquals("Data for " + key, dataObject.getData());
```

我们还可以使用getAll方法获取一组值：

```java
Map<String, DataObject> dataObjectMap 
  = cache.getAll(Arrays.asList("A", "B", "C"));

assertEquals(3, dataObjectMap.size());
```

从传递给构建方法的底层后端初始化函数中检索值。这使得使用缓存作为访问值的主要外观成为可能。

### 3.3. 异步加载

此策略与之前的策略相同，但异步执行操作并返回包含实际值的CompletableFuture ：

```java
AsyncLoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .maximumSize(100)
  .expireAfterWrite(1, TimeUnit.MINUTES)
  .buildAsync(k -> DataObject.get("Data for " + k));
```

我们可以以相同的方式使用get和getAll方法，考虑到它们返回CompletableFuture的事实：

```java
String key = "A";

cache.get(key).thenAccept(dataObject -> {
    assertNotNull(dataObject);
    assertEquals("Data for " + key, dataObject.getData());
});

cache.getAll(Arrays.asList("A", "B", "C"))
  .thenAccept(dataObjectMap -> assertEquals(3, dataObjectMap.size()));
```

CompletableFuture具有丰富且有用的 API，可以[在本文](https://www.baeldung.com/java-completablefuture)中阅读更多信息。

## 4. 价值驱逐

Caffeine 具有三种价值驱逐策略：基于大小、基于时间和基于引用。

### 4.1. 基于大小的驱逐

这种类型的逐出假设当超过配置的缓存大小限制时发生逐出。有两种获取大小的方法——对缓存中的对象进行计数，或者获取它们的权重。

让我们看看我们如何计算缓存中的对象。当缓存被初始化时，它的大小等于零：

```java
LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .maximumSize(1)
  .build(k -> DataObject.get("Data for " + k));

assertEquals(0, cache.estimatedSize());
```

当我们添加一个值时，大小明显增加：

```java
cache.get("A");

assertEquals(1, cache.estimatedSize());
```

我们可以将第二个值添加到缓存中，这会导致删除第一个值：

```java
cache.get("B");
cache.cleanUp();

assertEquals(1, cache.estimatedSize());
```

值得一提的是我们在获取缓存大小之前调用了cleanUp方法。这是因为缓存驱逐是异步执行的，这种方法有助于等待驱逐完成。

我们还可以通过一个weighter 函数 来获取缓存的大小：

```java
LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .maximumWeight(10)
  .weigher((k,v) -> 5)
  .build(k -> DataObject.get("Data for " + k));

assertEquals(0, cache.estimatedSize());

cache.get("A");
assertEquals(1, cache.estimatedSize());

cache.get("B");
assertEquals(2, cache.estimatedSize());
```

当权重超过 10 时，这些值将从缓存中删除：

```java
cache.get("C");
cache.cleanUp();

assertEquals(2, cache.estimatedSize());
```

### 4.2. 基于时间的驱逐

这种驱逐策略基于条目的过期时间，有以下三种：

-   访问后过期 - 自上次读取或写入发生后经过一段时间后条目过期
-   写入后过期——条目在自上次写入发生后经过一段时间后过期
-   自定义策略——过期时间由Expiry实现单独计算每个条目

让我们使用expireAfterAccess方法配置访问后过期策略：

```java
LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .expireAfterAccess(5, TimeUnit.MINUTES)
  .build(k -> DataObject.get("Data for " + k));
```

要配置写入后过期策略，我们使用expireAfterWrite方法：

```java
cache = Caffeine.newBuilder()
  .expireAfterWrite(10, TimeUnit.SECONDS)
  .weakKeys()
  .weakValues()
  .build(k -> DataObject.get("Data for " + k));
```

要初始化自定义策略，我们需要实现Expiry接口：

```java
cache = Caffeine.newBuilder().expireAfter(new Expiry<String, DataObject>() {
    @Override
    public long expireAfterCreate(
      String key, DataObject value, long currentTime) {
        return value.getData().length()  1000;
    }
    @Override
    public long expireAfterUpdate(
      String key, DataObject value, long currentTime, long currentDuration) {
        return currentDuration;
    }
    @Override
    public long expireAfterRead(
      String key, DataObject value, long currentTime, long currentDuration) {
        return currentDuration;
    }
}).build(k -> DataObject.get("Data for " + k));
```

### 4.3. 基于引用的驱逐

我们可以配置缓存以允许对缓存键和/或值进行垃圾回收。为此，我们将为键和值配置WeakRefence的使用，并且我们可以配置SoftReference仅用于值的垃圾收集。

WeakRefence用法允许在没有对对象的任何强引用时对对象进行垃圾回收。SoftReference允许基于 JVM 的全局最近最少使用策略对对象进行垃圾回收。可以在[此处](https://www.baeldung.com/java-weakhashmap)找到有关Java中引用的更多详细信息。

我们应该使用Caffeine.weakKeys()、Caffeine.weakValues()和Caffeine.softValues()来启用每个选项：

```java
LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .expireAfterWrite(10, TimeUnit.SECONDS)
  .weakKeys()
  .weakValues()
  .build(k -> DataObject.get("Data for " + k));

cache = Caffeine.newBuilder()
  .expireAfterWrite(10, TimeUnit.SECONDS)
  .softValues()
  .build(k -> DataObject.get("Data for " + k));
```

## 5.提神

可以将缓存配置为在定义的时间段后自动刷新条目。让我们看看如何使用refreshAfterWrite方法来做到这一点：

```java
Caffeine.newBuilder()
  .refreshAfterWrite(1, TimeUnit.MINUTES)
  .build(k -> DataObject.get("Data for " + k));
```

在这里我们应该了解expireAfter和refreshAfter之间的区别。当请求过期条目时，执行将阻塞，直到构建函数计算出新值。

但是，如果该条目符合刷新条件，则缓存将返回一个旧值并异步重新加载该值。

## 6.统计

Caffeine 有一种记录有关缓存使用情况的统计信息的方法：

```java
LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
  .maximumSize(100)
  .recordStats()
  .build(k -> DataObject.get("Data for " + k));
cache.get("A");
cache.get("A");

assertEquals(1, cache.stats().hitCount());
assertEquals(1, cache.stats().missCount());
```

我们还可以传递给recordStats供应商，它创建StatsCounter 的实现。每个与统计相关的更改都会推送此对象。

## 七. 总结

在本文中，我们熟悉了Java的 Caffeine 缓存库。我们看到了如何配置和填充缓存，以及如何根据我们的需要选择合适的过期或刷新策略。