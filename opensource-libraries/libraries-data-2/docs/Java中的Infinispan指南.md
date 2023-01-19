## 1. 概述

在本指南中，我们将了解[Infinispan](http://infinispan.org/)，这是一种内存中的键/值数据存储，与同类工具相比，它具有一组更强大的功能。

为了了解它是如何工作的，我们将构建一个简单的项目来展示最常见的功能并检查如何使用它们。

## 2.项目设置

为了能够以这种方式使用它，我们需要在pom.xml中添加它的依赖项。

最新版本可以在[Maven 中央](https://search.maven.org/classic/#search|gav|1|g%3A"org.infinispan" AND a%3A"infinispan-core")存储库中找到：

```xml
<dependency>
    <groupId>org.infinispan</groupId>
    <artifactId>infinispan-core</artifactId>
    <version>9.1.5.Final</version>
</dependency>
```

从现在开始，所有必要的底层基础设施都将以编程方式处理。

## 3.缓存管理器设置

CacheManager是我们将使用的大多数功能的基础。它充当所有已声明缓存的容器，控制它们的生命周期，并负责全局配置。

Infinispan 提供了一种非常简单的方法来构建CacheManager：

```java
public DefaultCacheManager cacheManager() {
    return new DefaultCacheManager();
}
```

现在我们可以用它来构建我们的缓存。

## 4.缓存设置

缓存由名称和配置定义。可以使用类ConfigurationBuilder构建必要的配置，该类已在我们的类路径中可用。

为了测试我们的缓存，我们将构建一个简单的方法来模拟一些繁重的查询：

```java
public class HelloWorldRepository {
    public String getHelloWorld() {
        try {
            System.out.println("Executing some heavy query");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ...
            e.printStackTrace();
        }
        return "Hello World!";
    }
}
```

此外，为了能够检查缓存中的更改，Infinispan 提供了一个简单的注解@Listener。

在定义我们的缓存时，我们可以传递一些对其中发生的任何事件感兴趣的对象，Infinispan 会在处理缓存时通知它：

```java
@Listener
public class CacheListener {
    @CacheEntryCreated
    public void entryCreated(CacheEntryCreatedEvent<String, String> event) {
        this.printLog("Adding key '" + event.getKey() 
          + "' to cache", event);
    }

    @CacheEntryExpired
    public void entryExpired(CacheEntryExpiredEvent<String, String> event) {
        this.printLog("Expiring key '" + event.getKey() 
          + "' from cache", event);
    }

    @CacheEntryVisited
    public void entryVisited(CacheEntryVisitedEvent<String, String> event) {
        this.printLog("Key '" + event.getKey() + "' was visited", event);
    }

    @CacheEntryActivated
    public void entryActivated(CacheEntryActivatedEvent<String, String> event) {
        this.printLog("Activating key '" + event.getKey() 
          + "' on cache", event);
    }

    @CacheEntryPassivated
    public void entryPassivated(CacheEntryPassivatedEvent<String, String> event) {
        this.printLog("Passivating key '" + event.getKey() 
          + "' from cache", event);
    }

    @CacheEntryLoaded
    public void entryLoaded(CacheEntryLoadedEvent<String, String> event) {
        this.printLog("Loading key '" + event.getKey() 
          + "' to cache", event);
    }

    @CacheEntriesEvicted
    public void entriesEvicted(CacheEntriesEvictedEvent<String, String> event) {
        StringBuilder builder = new StringBuilder();
        event.getEntries().forEach(
          (key, value) -> builder.append(key).append(", "));
        System.out.println("Evicting following entries from cache: " 
          + builder.toString());
    }

    private void printLog(String log, CacheEntryEvent event) {
        if (!event.isPre()) {
            System.out.println(log);
        }
    }
}
```

在打印我们的消息之前，我们检查被通知的事件是否已经发生，因为对于某些事件类型，Infinispan 会发送两个通知：一个在处理之前，一个在处理之后。

现在让我们构建一个方法来为我们处理缓存创建：

```java
private <K, V> Cache<K, V> buildCache(
  String cacheName, 
  DefaultCacheManager cacheManager, 
  CacheListener listener, 
  Configuration configuration) {

    cacheManager.defineConfiguration(cacheName, configuration);
    Cache<K, V> cache = cacheManager.getCache(cacheName);
    cache.addListener(listener);
    return cache;
}
```

请注意我们如何将配置传递给CacheManager，然后使用相同的cacheName来获取与所需缓存对应的对象。另请注意我们如何通知侦听器缓存对象本身。

我们现在将检查五种不同的缓存配置，我们将了解如何设置它们并充分利用它们。

### 4.1. 简单缓存

使用我们的方法buildCache可以在一行中定义最简单的缓存类型：

```java
public Cache<String, String> simpleHelloWorldCache(
  DefaultCacheManager cacheManager, 
  CacheListener listener) {
    return this.buildCache(SIMPLE_HELLO_WORLD_CACHE, 
      cacheManager, listener, new ConfigurationBuilder().build());
}
```

我们现在可以构建一个服务：

```java
public String findSimpleHelloWorld() {
    String cacheKey = "simple-hello";
    return simpleHelloWorldCache
      .computeIfAbsent(cacheKey, k -> repository.getHelloWorld());
}
```

请注意我们如何使用缓存，首先检查所需的条目是否已缓存。如果不是，我们需要调用我们的Repository然后缓存它。

让我们在我们的测试中添加一个简单的方法来计时我们的方法：

```java
protected <T> long timeThis(Supplier<T> supplier) {
    long millis = System.currentTimeMillis();
    supplier.get();
    return System.currentTimeMillis() - millis;
}
```

测试它，我们可以检查执行两个方法调用之间的时间：

```java
@Test
public void whenGetIsCalledTwoTimes_thenTheSecondShouldHitTheCache() {
    assertThat(timeThis(() -> helloWorldService.findSimpleHelloWorld()))
      .isGreaterThanOrEqualTo(1000);

    assertThat(timeThis(() -> helloWorldService.findSimpleHelloWorld()))
      .isLessThan(100);
}
```

### 4.2. 过期缓存

我们可以定义一个缓存，其中所有条目都有一个生命周期，换句话说，元素将在给定的时间段后从缓存中删除。配置非常简单：

```java
private Configuration expiringConfiguration() {
    return new ConfigurationBuilder().expiration()
      .lifespan(1, TimeUnit.SECONDS)
      .build();
}
```

现在我们使用上面的配置构建我们的缓存：

```java
public Cache<String, String> expiringHelloWorldCache(
  DefaultCacheManager cacheManager, 
  CacheListener listener) {
    
    return this.buildCache(EXPIRING_HELLO_WORLD_CACHE, 
      cacheManager, listener, expiringConfiguration());
}
```

最后，在我们上面的简单缓存中以类似的方法使用它：

```java
public String findSimpleHelloWorldInExpiringCache() {
    String cacheKey = "simple-hello";
    String helloWorld = expiringHelloWorldCache.get(cacheKey);
    if (helloWorld == null) {
        helloWorld = repository.getHelloWorld();
        expiringHelloWorldCache.put(cacheKey, helloWorld);
    }
    return helloWorld;
}
```

让我们再次测试我们的时间：

```java
@Test
public void whenGetIsCalledTwoTimesQuickly_thenTheSecondShouldHitTheCache() {
    assertThat(timeThis(() -> helloWorldService.findExpiringHelloWorld()))
      .isGreaterThanOrEqualTo(1000);

    assertThat(timeThis(() -> helloWorldService.findExpiringHelloWorld()))
      .isLessThan(100);
}
```

运行它，我们看到缓存快速连续命中。为了展示到期时间与其入场时间有关，让我们在我们的入场中强制使用它：

```java
@Test
public void whenGetIsCalledTwiceSparsely_thenNeitherHitsTheCache()
  throws InterruptedException {

    assertThat(timeThis(() -> helloWorldService.findExpiringHelloWorld()))
      .isGreaterThanOrEqualTo(1000);

    Thread.sleep(1100);

    assertThat(timeThis(() -> helloWorldService.findExpiringHelloWorld()))
      .isGreaterThanOrEqualTo(1000);
}
```

运行测试后，请注意在给定时间后我们的条目如何从缓存中过期。我们可以通过查看我们的侦听器打印的日志行来确认这一点：

```plaintext
Executing some heavy query
Adding key 'simple-hello' to cache
Expiring key 'simple-hello' from cache
Executing some heavy query
Adding key 'simple-hello' to cache
```

请注意，当我们尝试访问它时，该条目已过期。Infinispan 在两个时刻检查过期条目：当我们尝试访问它时或收割者线程扫描缓存时。

即使在主配置中没有它的缓存中，我们也可以使用过期。put方法接受更多参数：

```java
simpleHelloWorldCache.put(cacheKey, helloWorld, 10, TimeUnit.SECONDS);
```

或者，我们可以给我们的条目一个最大的空闲时间，而不是一个固定的生命周期：

```java
simpleHelloWorldCache.put(cacheKey, helloWorld, -1, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
```

将 -1 用于 lifetime 属性，缓存不会因此而过期，但是当我们将它与 10 秒的idleTime结合使用时，我们告诉 Infinispan 使该条目过期，除非它在此时间范围内被访问。

### 4.3. 缓存逐出

在 Infinispan 中，我们可以使用逐出配置限制给定缓存中的条目数：

```java
private Configuration evictingConfiguration() {
    return new ConfigurationBuilder()
      .memory().evictionType(EvictionType.COUNT).size(1)
      .build();
}
```

在此示例中，我们将此缓存中的最大条目数限制为一个，这意味着，如果我们尝试输入另一个条目，它将被从我们的缓存中逐出。

同样，该方法类似于此处已介绍的方法：

```java
public String findEvictingHelloWorld(String key) {
    String value = evictingHelloWorldCache.get(key);
    if(value == null) {
        value = repository.getHelloWorld();
        evictingHelloWorldCache.put(key, value);
    }
    return value;
}
```

让我们构建我们的测试：

```java
@Test
public void whenTwoAreAdded_thenFirstShouldntBeAvailable() {

    assertThat(timeThis(
      () -> helloWorldService.findEvictingHelloWorld("key 1")))
      .isGreaterThanOrEqualTo(1000);

    assertThat(timeThis(
      () -> helloWorldService.findEvictingHelloWorld("key 2")))
      .isGreaterThanOrEqualTo(1000);

    assertThat(timeThis(
      () -> helloWorldService.findEvictingHelloWorld("key 1")))
      .isGreaterThanOrEqualTo(1000);
}
```

运行测试，我们可以查看活动的侦听器日志：

```plaintext
Executing some heavy query
Adding key 'key 1' to cache
Executing some heavy query
Evicting following entries from cache: key 1, 
Adding key 'key 2' to cache
Executing some heavy query
Evicting following entries from cache: key 2, 
Adding key 'key 1' to cache
```

检查当我们插入第二个键时第一个键是如何自动从缓存中删除的，然后，第二个键也被删除以便再次为我们的第一个键腾出空间。

### 4.4. 钝化缓存

缓存钝化是 Infinispan 的强大功能之一。通过结合钝化和逐出，我们可以创建一个不占用大量内存的缓存，而不会丢失信息。

让我们看一下钝化配置：

```java
private Configuration passivatingConfiguration() {
    return new ConfigurationBuilder()
      .memory().evictionType(EvictionType.COUNT).size(1)
      .persistence() 
      .passivation(true)    // activating passivation
      .addSingleFileStore() // in a single file
      .purgeOnStartup(true) // clean the file on startup
      .location(System.getProperty("java.io.tmpdir")) 
      .build();
}
```

我们再次强制在我们的高速缓存中只输入一个条目，但告诉 Infinispan 钝化剩余的条目，而不是仅仅删除它们。

让我们看看当我们尝试填写多个条目时会发生什么：

```java
public String findPassivatingHelloWorld(String key) {
    return passivatingHelloWorldCache.computeIfAbsent(key, k -> 
      repository.getHelloWorld());
}
```

让我们构建我们的测试并运行它：

```java
@Test
public void whenTwoAreAdded_thenTheFirstShouldBeAvailable() {

    assertThat(timeThis(
      () -> helloWorldService.findPassivatingHelloWorld("key 1")))
      .isGreaterThanOrEqualTo(1000);

    assertThat(timeThis(
      () -> helloWorldService.findPassivatingHelloWorld("key 2")))
      .isGreaterThanOrEqualTo(1000);

    assertThat(timeThis(
      () -> helloWorldService.findPassivatingHelloWorld("key 1")))
      .isLessThan(100);
}
```

现在让我们看看我们的侦听器活动：

```plaintext
Executing some heavy query
Adding key 'key 1' to cache
Executing some heavy query
Passivating key 'key 1' from cache
Evicting following entries from cache: key 1, 
Adding key 'key 2' to cache
Passivating key 'key 2' from cache
Evicting following entries from cache: key 2, 
Loading key 'key 1' to cache
Activating key 'key 1' on cache
Key 'key 1' was visited
```

注意需要多少步才能使我们的缓存只包含一个条目。另外，请注意步骤的顺序——钝化、逐出、加载和激活。让我们看看这些步骤的含义：

-   钝化——我们的条目存储在另一个地方，远离 Infinispan 的主存储(在本例中为内存)
-   逐出——条目被删除，以释放内存并在缓存中保留配置的最大条目数
-   加载——当试图访问我们的钝化条目时，Infinispan 检查它的存储内容并再次将条目加载到内存中
-   激活——该条目现在可以再次在 Infinispan 中访问

### 4.5. 事务缓存

Infinispan 带有强大的事务控制。与数据库对应物一样，它有助于在多个线程尝试写入同一条目时保持完整性。

让我们看看如何定义具有事务功能的缓存：

```java
private Configuration transactionalConfiguration() {
    return new ConfigurationBuilder()
      .transaction().transactionMode(TransactionMode.TRANSACTIONAL)
      .lockingMode(LockingMode.PESSIMISTIC)
      .build();
}
```

为了能够对其进行测试，让我们构建两种方法——一种快速完成其事务，另一种需要一段时间：

```java
public Integer getQuickHowManyVisits() {
    TransactionManager tm = transactionalCache
      .getAdvancedCache().getTransactionManager();
    tm.begin();
    Integer howManyVisits = transactionalCache.get(KEY);
    howManyVisits++;
    System.out.println("I'll try to set HowManyVisits to " + howManyVisits);
    StopWatch watch = new StopWatch();
    watch.start();
    transactionalCache.put(KEY, howManyVisits);
    watch.stop();
    System.out.println("I was able to set HowManyVisits to " + howManyVisits + 
      " after waiting " + watch.getTotalTimeSeconds() + " seconds");

    tm.commit();
    return howManyVisits;
}
public void startBackgroundBatch() {
    TransactionManager tm = transactionalCache
      .getAdvancedCache().getTransactionManager();
    tm.begin();
    transactionalCache.put(KEY, 1000);
    System.out.println("HowManyVisits should now be 1000, " +
      "but we are holding the transaction");
    Thread.sleep(1000L);
    tm.rollback();
    System.out.println("The slow batch suffered a rollback");
}
```

现在让我们创建一个测试来执行这两种方法并检查 Infinispan 的行为：

```java
@Test
public void whenLockingAnEntry_thenItShouldBeInaccessible() throws InterruptedException {
    Runnable backGroundJob = () -> transactionalService.startBackgroundBatch();
    Thread backgroundThread = new Thread(backGroundJob);
    transactionalService.getQuickHowManyVisits();
    backgroundThread.start();
    Thread.sleep(100); //lets wait our thread warm up

    assertThat(timeThis(() -> transactionalService.getQuickHowManyVisits()))
      .isGreaterThan(500).isLessThan(1000);
}
```

执行它，我们将再次在控制台中看到以下活动：

```plaintext
Adding key 'key' to cache
Key 'key' was visited
Ill try to set HowManyVisits to 1
I was able to set HowManyVisits to 1 after waiting 0.001 seconds
HowManyVisits should now be 1000, but we are holding the transaction
Key 'key' was visited
Ill try to set HowManyVisits to 2
I was able to set HowManyVisits to 2 after waiting 0.902 seconds
The slow batch suffered a rollback
```

在主线程查看时间，等待slow方法创建的事务结束。

## 5.总结

在本文中，我们了解了 Infinispan 是什么，以及它在应用程序中作为缓存的领先特性和功能。