## 1. 概述

集合是几乎所有现代应用程序中常见的基本构建块。因此， Redis 提供了多种[流行的数据结构](https://redis.io/topics/data-types) ，如列表、集合、哈希和排序集合供我们使用也就不足为奇了。

在本教程中，我们将学习如何有效地读取与特定模式匹配的所有可用 Redis 键。

## 2.探索收藏

假设我们的应用程序使用 Redis 来存储有关不同运动中使用的球的信息。我们应该能够看到有关 Redis 集合中可用的每个球的信息。为简单起见，我们将数据集限制为只有三个球：

-   板球重 160 克
-   重量为 450 克的足球
-   重量为 270 克的排球

像往常一样，让我们首先通过一种简单的方法来探索 Redis 集合来理清我们的基础知识。

## 3. 使用 redis-cli 的简单方法

在我们开始编写Java代码来探索集合之前，我们应该清楚地了解我们将如何使用[redis-cli](https://redis.io/topics/rediscli)界面来完成它。假设我们的 Redis 实例在127.0.0.1端口6379上可用，以便我们使用命令行界面探索每种集合类型。

### 3.1. 链表

[首先，让我们在rpush](https://redis.io/commands/rpush)命令的帮助下，以sports-name _ ball-weight 的格式将我们的数据集存储在名为balls的 Redis 链表中：

```shell
% redis-cli -h 127.0.0.1 -p 6379
127.0.0.1:6379> RPUSH balls "cricket_160"
(integer) 1
127.0.0.1:6379> RPUSH balls "football_450"
(integer) 2
127.0.0.1:6379> RPUSH balls "volleyball_270"
(integer) 3
```

我们可以注意到，成功插入列表会输出列表的新长度。然而，在大多数情况下，我们将对数据插入活动视而不见。因此，我们可以使用[llen](https://redis.io/commands/llen)命令找出链表的长度：

```shell
127.0.0.1:6379> llen balls
(integer) 3
```

当我们已经知道列表的长度时，可以方便地使用[lrange](https://redis.io/commands/lrange)命令轻松检索整个数据集：

```shell
127.0.0.1:6379> lrange balls 0 2
1) "cricket_160"
2) "football_450"
3) "volleyball_270"
```

### 3.2. 放

接下来，让我们看看当我们决定将数据集存储在 Redis 集中时，我们如何探索数据集。为此，我们首先需要使用[sadd](https://redis.io/commands/sadd)命令将我们的数据集填充到名为 balls 的 Redis 集中：

```shell
127.0.0.1:6379> sadd balls "cricket_160" "football_450" "volleyball_270" "cricket_160"
(integer) 3
```

哎呀！我们的命令中有一个重复值。但是，因为我们是在向集合中添加值，所以我们不需要担心重复项。当然，我们可以从输出的 response-value 中看到添加的项目数。

现在，我们可以利用[smembers](https://redis.io/commands/smembers)命令查看所有集合成员：

```shell
127.0.0.1:6379> smembers balls
1) "volleyball_270"
2) "cricket_160"
3) "football_450"
```

### 3.3. 散列

现在，让我们使用 Redis 的散列数据结构将我们的数据集存储在一个名为 balls 的散列键中，这样散列的字段是运动名称，字段值是球的重量。[我们可以在hmset](https://redis.io/commands/hmset)命令的帮助下做到这一点：

```shell
127.0.0.1:6379> hmset balls cricket 160 football 450 volleyball 270
OK
```

要查看存储在哈希中的信息，我们可以使用[hgetall](https://redis.io/commands/hgetall)命令：

```shell
127.0.0.1:6379> hgetall balls
1) "cricket"
2) "160"
3) "football"
4) "450"
5) "volleyball"
6) "270"
```

### 3.4. 排序集

除了唯一的成员值之外，排序集还允许我们在它们旁边保留一个分数。那么，在我们的用例中，我们可以将运动名称作为成员值，将球的重量作为分数。让我们使用[zadd](https://redis.io/commands/zadd)命令来存储我们的数据集：

```shell
127.0.0.1:6379> zadd balls 160 cricket 450 football 270 volleyball
(integer) 3
```

现在，我们可以先使用[zcard](https://redis.io/commands/zcard)命令查找有序集的长度，然后使用[zrange](https://redis.io/commands/zrange)命令探索完整集：

```shell
127.0.0.1:6379> zcard balls
(integer) 3
127.0.0.1:6379> zrange balls 0 2
1) "cricket"
2) "volleyball"
3) "football"
```

### 3.5. 字符串

我们还可以将通常的键值字符串视为项的表面集合。[让我们首先使用mset](https://redis.io/commands/mset)命令填充我们的数据集：

```shell
127.0.0.1:6379> mset balls:cricket 160 balls:football 450 balls:volleyball 270
OK
```

我们必须注意，我们添加了前缀“balls: ”，以便我们可以从可能位于 Redis 数据库中的其余键中识别出这些键。此外，这种命名策略允许我们使用[keys](https://redis.io/commands/keys)命令在前缀模式匹配的帮助下探索我们的数据集：

```shell
127.0.0.1:6379> keys balls
1) "balls:cricket"
2) "balls:volleyball"
3) "balls:football"
```

## 4. 朴素的Java实现

现在我们已经对可用于探索不同类型的集合的相关 Redis 命令有了基本的了解，现在是我们动手编写代码的时候了。

### 4.1. Maven 依赖

在本节中，我们将在我们的实现中使用 Redis 的[Jedis](https://www.baeldung.com/jedis-java-redis-client-library)客户端库：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.2.0</version>
</dependency>
```

### 4.2. Redis客户端

Jedis 库带有 Redis-CLI 类名方法。但是，建议我们创建一个包装器 Redis 客户端，它将在内部调用 Jedis 函数调用。

每当我们使用 Jedis 库时，我们必须牢记单个 Jedis 实例不是线程安全的。因此，要在我们的应用程序中获取 Jedis 资源，我们可以使用[JedisPool](https://www.baeldung.com/jedis-java-redis-client-library#Connection)，它是一个线程安全的网络连接池。

而且，由于我们不希望在应用程序生命周期的任何给定时间出现多个 Redis 客户端实例，因此我们应该根据[单例设计模式](https://www.baeldung.com/java-singleton-double-checked-locking)的原则创建RedisClient类。

首先，让我们为我们的客户端创建一个私有构造函数，它将在创建RedisClient类的实例时在内部初始化JedisPool ：

```java
private static JedisPool jedisPool;

private RedisClient(String ip, int port) {
    try {
        if (jedisPool == null) {
            jedisPool = new JedisPool(new URI("http://" + ip + ":" + port));
        }
    } catch (URISyntaxException e) {
        log.error("Malformed server address", e);
    }
}
```

接下来，我们需要一个访问单例客户端的点。因此，让我们为此创建一个静态方法getInstance() ：

```java
private static volatile RedisClient instance = null;

public static RedisClient getInstance(String ip, final int port) {
    if (instance == null) {
        synchronized (RedisClient.class) {
            if (instance == null) {
                instance = new RedisClient(ip, port);
            }
        }
    }
    return instance;
}
```

最后，让我们看看如何在 Jedis 的lrange 方法之上创建一个包装器方法：

```java
public List lrange(final String key, final long start, final long stop) {
    try (Jedis jedis = jedisPool.getResource()) {
        return jedis.lrange(key, start, stop);
    } catch (Exception ex) {
        log.error("Exception caught in lrange", ex);
    }
    return new LinkedList();
}
```

当然，我们可以按照相同的策略创建其余的包装器方法，例如lpush、hmset、hgetall、sadd、smembers、keys、zadd和zrange。

### 4.3. 分析

在最好的情况下，我们可以用来一次性探索集合的所有 Redis 命令自然会具有 O(n) 时间复杂度。

我们可能有点自由，称这种方法为幼稚。在 Redis 的实际生产实例中，单个集合中有数千或数百万个键是很常见的。此外，[Redis 的单线程特性](https://redis.io/topics/latency#single-threaded-nature-of-redis)带来了更多的痛苦，我们的方法可能会灾难性地阻止其他更高优先级的操作。

因此，我们应该明确指出，我们限制我们的天真方法仅用于调试目的。

## 5. 迭代器基础

我们天真的实现中的主要缺陷是我们要求 Redis 一次为我们提供单个获取查询的所有结果。为了克服这个问题，我们可以将原始的提取查询分解为多个顺序提取查询，这些查询对整个数据集的较小块进行操作。

假设我们有一本我们应该阅读的 1,000 页的书。如果我们按照我们天真的方法，我们将不得不一口气读完这本大书，中间没有任何休息。这对我们的健康来说是致命的，因为它会耗尽我们的能量并阻止我们进行任何其他更重要的活动。

当然，正确的方法是通过多次阅读来完成这本书。在每个会话中，我们都从上一个会话中断的地方继续——我们可以使用页面书签来跟踪我们的进度。

尽管两种情况下的总阅读时间具有可比性，但第二种方法更好，因为它给了我们喘息的空间。

让我们看看如何使用基于迭代器的方法来探索 Redis 集合。

## 6.Redis扫描

Redis 提供了多种扫描策略来使用基于游标的方法从集合中读取键，这在原则上类似于页面书签。

### 6.1. 扫描策略

[我们可以使用Scan](https://redis.io/commands/scan)命令扫描整个键值集合存储。但是，如果我们想通过集合类型来限制我们的数据集，那么我们可以使用其中一种变体：

-   [Sscan](https://redis.io/commands/sscan)可用于遍历集合
-   [Hscan](https://redis.io/commands/hscan)帮助我们遍历散列中的字段值对
-   [Zscan](https://redis.io/commands/zscan)允许对存储在有序集合中的成员进行迭代

我们必须注意，我们并不真的需要专门为链表设计的服务器端扫描策略。[那是因为我们可以使用lindex](https://redis.io/commands/lindex)或[lrange](https://redis.io/commands/lrange)命令通过索引访问链表的成员。另外，我们可以找出元素的数量并在一个简单的循环中使用lrange以小块的形式迭代整个列表。

让我们使用SCAN命令来扫描字符串类型的键。要开始扫描，我们需要使用光标值为“0”，匹配模式字符串为“ball”：

```shell
127.0.0.1:6379> mset balls:cricket 160 balls:football 450 balls:volleyball 270
OK
127.0.0.1:6379> SCAN 0 MATCH ball COUNT 1
1) "2"
2) 1) "balls:cricket"
127.0.0.1:6379> SCAN 2 MATCH ball COUNT 1
1) "3"
2) 1) "balls:volleyball"
127.0.0.1:6379> SCAN 3 MATCH ball COUNT 1
1) "0"
2) 1) "balls:football"
```

每次完成扫描后，我们都会获得要在后续迭代中使用的下一个游标值。最终，当下一个游标值为“0”时，我们知道我们已经扫描了整个集合。

## 7. 使用Java扫描

到目前为止，我们对我们的方法有了足够的了解，可以开始在Java中实现它。

### 7.1. 扫描策略

如果我们深入了解Jedis类提供的核心扫描功能，我们将找到扫描不同集合类型的策略：

```java
public ScanResult<String> scan(final String cursor, final ScanParams params);
public ScanResult<String> sscan(final String key, final String cursor, final ScanParams params);
public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor,
  final ScanParams params);
public ScanResult<Tuple> zscan(final String key, final String cursor, final ScanParams params);
```

Jedis需要两个可选参数，search-pattern 和 result-size，来有效地控制扫描——ScanParams使之成为可能。为此，它依赖于match()和count()方法，它们大致基于[构建器设计模式](https://www.baeldung.com/creational-design-patterns#builder)：

```java
public ScanParams match(final String pattern);
public ScanParams count(final Integer count);
```

现在我们已经了解了Jedis扫描方法的基本知识，让我们通过ScanStrategy接口对这些策略进行建模：

```java
public interface ScanStrategy<T> {
    ScanResult<T> scan(Jedis jedis, String cursor, ScanParams scanParams);
}
```

首先，让我们研究最简单的扫描策略，它独立于集合类型并读取键，但不读取键的值：

```java
public class Scan implements ScanStrategy<String> {
    public ScanResult<String> scan(Jedis jedis, String cursor, ScanParams scanParams) {
        return jedis.scan(cursor, scanParams);
    }
}
```

接下来，让我们选择hscan策略，该策略专门用于读取特定哈希键的所有字段键和字段值：

```java
public class Hscan implements ScanStrategy<Map.Entry<String, String>> {

    private String key;

    @Override
    public ScanResult<Entry<String, String>> scan(Jedis jedis, String cursor, ScanParams scanParams) {
        return jedis.hscan(key, cursor, scanParams);
    }
}
```

最后，让我们构建集合和有序集合的策略。sscan策略可以读取一个集合的所有成员，而zscan策略可以以Tuple s的形式读取成员及其分数：

```java
public class Sscan implements ScanStrategy<String> {

    private String key;

    public ScanResult<String> scan(Jedis jedis, String cursor, ScanParams scanParams) {
        return jedis.sscan(key, cursor, scanParams);
    }
}

public class Zscan implements ScanStrategy<Tuple> {

    private String key;

    @Override
    public ScanResult<Tuple> scan(Jedis jedis, String cursor, ScanParams scanParams) {
        return jedis.zscan(key, cursor, scanParams);
    }
}
```

### 7.2. 返回迭代器

接下来，让我们勾勒出构建RedisIterator类所需的构建块：

-   基于字符串的游标
-   扫描策略，如scan、 sscan 、hscan、zscan
-   扫描参数的占位符
-   访问JedisPool获取Jedis资源

我们现在可以继续在我们的RedisIterator类中定义这些成员：

```java
private final JedisPool jedisPool;
private ScanParams scanParams;
private String cursor;
private ScanStrategy<T> strategy;
```

我们的阶段已全部设置为为我们的迭代器定义特定于迭代器的功能。为此，我们的RedisIterator类必须实现[Iterator](https://www.baeldung.com/java-iterator)接口：

```java
public class RedisIterator<T> implements Iterator<List<T>> {
}
```

自然地，我们需要覆盖继承自Iterator接口的hasNext()和next()方法。

首先，让我们选择容易获得的成果——hasNext()方法——因为底层逻辑是直截了当的。一旦光标值变为“0”，我们就知道我们已经完成了扫描。那么，让我们看看如何在一行中实现这一点：

```java
@Override
public boolean hasNext() {
    return !"0".equals(cursor);
}
```

接下来，让我们研究执行繁重扫描的next()方法：

```java
@Override
public List next() {
    if (cursor == null) {
        cursor = "0";
    }
    try (Jedis jedis = jedisPool.getResource()) {
        ScanResult scanResult = strategy.scan(jedis, cursor, scanParams);
        cursor = scanResult.getCursor();
        return scanResult.getResult();
    } catch (Exception ex) {
        log.error("Exception caught in next()", ex);
    }
    return new LinkedList();
}
```

需要注意的是，ScanResult不仅给出了扫描结果，还给出了后续扫描需要的下一个游标值。

最后，我们可以启用在RedisClient类中创建RedisIterator的功能：

```java
public RedisIterator iterator(int initialScanCount, String pattern, ScanStrategy strategy) {
    return new RedisIterator(jedisPool, initialScanCount, pattern, strategy);
}
```

### 7.3. 使用 Redis 迭代器读取

由于我们在Iterator接口的帮助下设计了 Redis 迭代器，只要hasNext()返回true ，在next()方法的帮助下读取集合值就非常直观。

为了完整和简单起见，我们将首先将与运动球相关的数据集存储在 Redis 哈希中。之后，我们将使用我们的RedisClient创建一个使用Hscan扫描策略的迭代器。让我们通过实际操作来测试我们的实现：

```java
@Test
public void testHscanStrategy() {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("cricket", "160");
    hash.put("football", "450");
    hash.put("volleyball", "270");
    redisClient.hmset("balls", hash);

    Hscan scanStrategy = new Hscan("balls");
    int iterationCount = 2;
    RedisIterator iterator = redisClient.iterator(iterationCount, "", scanStrategy);
    List<Map.Entry<String, String>> results = new LinkedList<Map.Entry<String, String>>();
    while (iterator.hasNext()) {
        results.addAll(iterator.next());
    }
    Assert.assertEquals(hash.size(), results.size());
}
```

我们可以按照相同的思维过程进行少量修改，以测试和实施剩余的策略来扫描和读取不同类型集合中可用的键。

## 八. 总结

我们开始本教程的目的是了解如何读取 Redis 中的所有匹配键。

我们发现 Redis 提供了一种简单的方法来一次读取键。虽然简单，但我们讨论了这如何对资源造成压力，因此不适合生产系统。深入挖掘后，我们了解到有一种基于迭代器的方法可以通过匹配的 Redis 键扫描我们的读取查询。