## 1. 概述

在本教程中，我们将了解[cache2k](https://cache2k.org/) — 一个轻量级、高性能、内存中的Java缓存库。

## 2.关于cache2k

由于对缓存值的非阻塞和无等待访问，cache2k 库提供了快速访问时间。它还支持与 Spring Framework、Scala Cache、Datanucleus 和 Hibernate 的集成。

该库具有许多功能，包括一组线程安全的原子操作、具有阻塞通读功能的缓存加载器 、自动过期、提前刷新、事件侦听器以及对 JSR107 API 的[JCache](https://www.baeldung.com/jcache)实现的支持。我们将在本教程中讨论其中的一些功能。

重要的是要注意 cache2k 不是像[Infispan](https://www.baeldung.com/infinispan)或[Hazelcast](https://www.baeldung.com/java-hazelcast)这样的分布式缓存解决方案。

## 3.Maven依赖

要使用 cache2k，我们需要首先将[cache2k-base-bom依赖](https://search.maven.org/classic/#search|gav|1|g%3A"org.cache2k" AND a%3A"cache2k-base-bom")项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.cache2k</groupId>
    <artifactId>cache2k-base-bom</artifactId>
    <version>1.2.3.Final</version>
    <type>pom</type>
</dependency>
```

## 4. 一个简单的cache2k例子

现在，让我们通过一个简单的例子看看如何在Java应用程序中使用 cache2k。

让我们考虑一个在线购物网站的例子。假设该网站对所有运动产品提供 20% 的折扣，对其他产品提供 10% 的折扣。我们的目标是缓存折扣，这样我们就不会每次都计算它。

因此，首先，我们将创建一个ProductHelper类并创建一个简单的缓存实现：

```java
public class ProductHelper {

    private Cache<String, Integer> cachedDiscounts;
    private int cacheMissCount = 0;

    public ProductHelper() {
        cachedDiscounts = Cache2kBuilder.of(String.class, Integer.class)
          .name("discount")
          .eternal(true)
          .entryCapacity(100)
          .build();
    }

    public Integer getDiscount(String productType) {
        Integer discount = cachedDiscounts.get(productType);
        if (Objects.isNull(discount)) {
            cacheMissCount++;
            discount = "Sports".equalsIgnoreCase(productType) ? 20 : 10;
            cachedDiscounts.put(productType, discount);
        }
        return discount;
    }

    // Getters and setters

}
```

如我们所见，我们使用了cacheMissCount变量来计算在缓存中未找到折扣的次数。因此，如果getDiscount方法使用缓存来获取折扣，则cacheMissCount不会发生变化。

接下来，我们将编写一个测试用例并验证我们的实现：

```java
@Test
public void whenInvokedGetDiscountTwice_thenGetItFromCache() {
    ProductHelper productHelper = new ProductHelper();
    assertTrue(productHelper.getCacheMissCount() == 0);
    
    assertTrue(productHelper.getDiscount("Sports") == 20);
    assertTrue(productHelper.getDiscount("Sports") == 20);
    
    assertTrue(productHelper.getCacheMissCount() == 1);
}
```

最后，让我们快速浏览一下我们使用的配置。

第一个是name方法，它设置我们缓存的唯一名称。缓存名称是可选的，如果我们不提供它，则会生成它。

然后，我们将eternal设置为true以指示缓存值不会随时间过期。因此，在这种情况下，我们可以选择显式地从缓存中删除元素。否则，一旦缓存达到其容量，元素将被自动驱逐。

此外，我们还使用了entryCapacity方法来指定缓存保留的最大条目数。当缓存达到最大大小时，缓存逐出算法将删除一个或多个条目以维持指定的容量。

我们可以进一步探索[Cache2kBuilder](https://cache2k.org/docs/latest/apidocs/cache2k-api/org/cache2k/Cache2kBuilder.html)类中的其他可用配置。

## 5. cache2k特点

现在，让我们增强示例以探索一些 cache2k 功能。

### 5.1. 配置缓存过期

到目前为止，我们对所有运动产品都允许固定折扣。但是，我们的网站现在希望仅在固定时间段内提供折扣。

为了满足这个新要求，我们将使用expireAfterWrite方法配置缓存过期：

```java
cachedDiscounts = Cache2kBuilder.of(String.class, Integer.class)
  // other configurations
  .expireAfterWrite(10, TimeUnit.MILLISECONDS)
  .build();
```

现在让我们编写一个测试用例来检查缓存过期：

```java
@Test
public void whenInvokedGetDiscountAfterExpiration_thenDiscountCalculatedAgain() 
  throws InterruptedException {
    ProductHelper productHelper = new ProductHelper();
    assertTrue(productHelper.getCacheMissCount() == 0);
    assertTrue(productHelper.getDiscount("Sports") == 20);
    assertTrue(productHelper.getCacheMissCount() == 1);

    Thread.sleep(20);

    assertTrue(productHelper.getDiscount("Sports") == 20);
    assertTrue(productHelper.getCacheMissCount() == 2);
}
```

在我们的测试用例中，我们尝试在配置的持续时间过后再次获得折扣。我们可以看到，与前面的示例不同，cacheMissCount已递增。这是因为缓存中的商品已过期，重新计算折扣。

对于高级缓存过期配置，我们还可以配置[ExpiryPolicy](https://cache2k.org/docs/latest/apidocs/cache2k-api/org/cache2k/expiry/ExpiryPolicy.html)。

### 5.2. 缓存加载或通读

在我们的示例中，我们使用缓存备用模式来加载缓存。这意味着我们已经在getDiscount方法中按需计算并添加了缓存中的折扣。

或者，我们可以简单地使用对 read-through 操作的 cache2k 支持。在此操作中，缓存将在加载器的帮助下自行加载缺失值。这也称为缓存加载。

现在，让我们进一步增强我们的示例以自动计算和加载缓存：

```java
cachedDiscounts = Cache2kBuilder.of(String.class, Integer.class)
  // other configurations
  .loader((key) -> {
      cacheMissCount++;
      return "Sports".equalsIgnoreCase(key) ? 20 : 10;
  })
  .build();
```

此外，我们将从getDiscount中删除计算和更新折扣的逻辑：

```java
public Integer getDiscount(String productType) {
    return cachedDiscounts.get(productType);
}
```

之后，让我们编写一个测试用例以确保加载器按预期工作：

```java
@Test
public void whenInvokedGetDiscount_thenPopulateCacheUsingLoader() {
    ProductHelper productHelper = new ProductHelper();
    assertTrue(productHelper.getCacheMissCount() == 0);

    assertTrue(productHelper.getDiscount("Sports") == 20);
    assertTrue(productHelper.getCacheMissCount() == 1);

    assertTrue(productHelper.getDiscount("Electronics") == 10);
    assertTrue(productHelper.getCacheMissCount() == 2);
}
```

### 5.3. 事件监听器

我们还可以为不同的缓存操作配置事件侦听器，例如缓存元素的插入、更新、删除和过期。

假设我们要记录所有添加到缓存中的条目。因此，让我们在缓存构建器中添加一个事件侦听器配置：

```java
.addListener(new CacheEntryCreatedListener<String, Integer>() {
    @Override
    public void onEntryCreated(Cache<String, Integer> cache, CacheEntry<String, Integer> entry) {
        LOGGER.info("Entry created: [{}, {}].", entry.getKey(), entry.getValue());
    }
})
```

现在，我们可以执行我们创建的任何测试用例并验证日志：

```plaintext
Entry created: [Sports, 20].
```

重要的是要注意事件侦听器同步执行，但到期事件除外。如果我们想要一个异步监听器，我们可以使用addAsyncListener方法。

### 5.4. 原子操作

[Cache](https://cache2k.org/docs/latest/apidocs/cache2k-api/org/cache2k/Cache.html)类有许多支持原子操作的方法。这些方法仅适用于对单个条目的操作。

这些方法包括containsAndRemove 、 putIfAbsent 、 removeIfEquals 、 replaceIfEquals、peekAndReplace和peekAndPut。

## 六. 总结

在本教程中，我们研究了 cache2k 库及其一些有用的功能。我们可以参考[cache2k 用户指南](https://cache2k.org/docs/latest/user-guide.html)来进一步探索这个库。