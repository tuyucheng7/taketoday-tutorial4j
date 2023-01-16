## 1. 概述

在 Redis 中缓存时，当缓存失效时清除整个缓存会很有用。

在这个简短的教程中，我们将学习如何删除 Redis 中存在的所有键，包括特定数据库和所有数据库中的键。

首先，我们将查看命令行。然后，我们将了解如何使用 API 和Java客户端完成同样的事情。

## 2.运行Redis

我们需要安装 Redis 才能使用。[Redis 快速入门](https://redis.io/topics/quickstart)中有适用于 Mac 和 Linux 的安装说明。在 docker 中运行 Redis 可能更容易。

让我们启动一个测试 Redis 服务器：

```bash
docker run --name redis -p 6379:6379 -d redis:latest
```

而且，我们可以运行[redis-cli ](https://redis.io/topics/rediscli)来测试这个服务器是否正常工作：

```bash
docker exec -it redis redis-cli
```

这会将我们带入 cli shell，其中命令 ping将测试服务器是否已启动：

```bash
127.0.0.1:6379> ping
PONG
```

我们使用 CTRL+C退出 redis-cli 。

## 3.Redis命令

让我们从删除所有内容的 Redis 命令开始。

有两个主要命令可以删除 Redis 中存在的键：FLUSHDB和FLUSHALL。我们可以使用 Redis CLI 来执行这些命令。

FLUSHDB命令删除数据库中的键。FLUSHALL命令删除所有数据库中的所有键。

我们可以使用ASYNC选项在后台线程中执行这些操作。如果刷新需要很长时间，这很有用，因为使命令ASYNC阻止它阻塞直到完成。

我们应该注意到 ASYNC选项从 Redis 4.0.0 开始可用。

## 4. 使用Java客户端

现在，让我们看看如何使用[Jedis](https://www.baeldung.com/jedis-java-redis-client-library)Java客户端删除密钥。

### 4.1. 依赖关系

首先，我们需要为[Jedis](https://search.maven.org/classic/#search|gav|1|g%3A"redis.clients" AND a%3A"jedis")添加 Maven 依赖项：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.3.0</version>
</dependency>
```

为了使测试更容易，我们还使用[嵌入式 Redis 服务器](https://search.maven.org/classic/#search|ga|1|g%3A"com.github.kstyrc" AND a%3A"embedded-redis")：

```xml
<dependency>
    <groupId>com.github.kstyrc</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>0.6</version>
</dependency>
```

### 4.2. 启动嵌入式 Redis

我们将创建一个嵌入式 Redis 服务器进行测试，方法是在可用端口上运行它：

```java
RedisService redisServer = new RedisServer(port);
```

然后使用localhost作为主机名和相同的端口创建我们的 Jedis 客户端：

```java
Jedis jedis = new Jedis("localhost", port);
```

## 5. 刷新单个数据库

让我们将一些数据放入数据库并检查它是否被记住：

```java
String key = "key";
String value = "value";
jedis.set(key, value);
String received = jedis.get(key);
 
assertEquals(value, received);
```

现在让我们使用flushDB方法刷新数据库：

```java
jedis.flushDB();

assertNull(jedis.get(key));
```

如我们所见，尝试在刷新后检索值返回null。

## 6.清除所有数据库

Redis 提供了多个数据库，这些数据库是有编号的。在添加值之前，我们可以使用select命令将数据添加到不同的数据库：

```java
jedis.select(0);
jedis.set("key1", "value1");
jedis.select(1);
jedis.set("key2", "value2");
```

我们现在应该在我们的两个数据库中各有一个密钥：

```java
jedis.select(0);
assertEquals("value1", jedis.get("key1"));
assertNull(jedis.get("key2"));

jedis.select(1);
assertEquals("value2", jedis.get("key2"));
assertNull(jedis.get("key1"));
```

flushDB方法只会清除当前数据库。为了清除所有数据库，我们使用flushAll 方法：

```java
jedis.flushAll();
```

我们可以测试这是否有效：

```java
jedis.select(0);
 
assertNull(jedis.get("key1"));
assertNull(jedis.get("key2"));
 
jedis.select(1);
 
assertNull(jedis.get("key1"));
assertNull(jedis.get("key2"));
```

## 7. 时间复杂度

Redis 是一种快速的数据存储，可以很好地扩展。但是，当有更多数据时，刷新操作可能需要更长的时间。

FLUSHDB操作的[时间复杂度](https://www.baeldung.com/java-algorithm-complexity)为O(N)，其中N是数据库中键的数量。如果我们使用FLUSHALL命令，时间复杂度又是O(N)，但这里N是所有数据库中的键数。

## 八. 总结

在本文中，我们了解了如何在 Docker 中运行 Redis 和redis-cli，以及如何将 Jedis 客户端用于带有嵌入式 Redis 服务器的 Java。

我们看到了如何将数据保存在不同的 Redis 数据库中，以及如何使用 flush 命令清除其中一个或多个数据库。