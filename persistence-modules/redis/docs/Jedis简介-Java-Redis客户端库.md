## 1. 概述

在本教程中，我们将介绍 Jedis，这是一个用于[Redis](http://redis.io/)的Java客户端库。这种流行的内存数据结构存储也可以持久保存在磁盘上。它由基于密钥库的数据结构驱动以持久化数据，可用作数据库、缓存、消息代理等。

我们将从讨论什么是 Jedis 开始，以及它在什么样的情况下有用。然后我们将详细介绍各种数据结构，并解释事务、流水线和发布/订阅功能。最后，我们将了解连接池和 Redis 集群。

## 2. 为什么是绝地武士？

[Redis 在其官方网站](http://redis.io/clients#java/)上列出了最著名的客户端库。Jedis 有多个替代方案，但目前值得推荐的只有两个 star，[lettuce](https://paluch.biz/)和[Redisson](https://github.com/mrniko/redisson/)。

这两个客户端确实有一些独特的特性，比如线程安全、透明的重新连接处理和异步 API，这些都是 Jedis 所缺乏的。

然而，Jedis 很小，而且比其他两个快得多。此外，它是 Spring Framework 开发人员的首选客户端库，并且拥有三者中最大的社区。

## 3.Maven依赖

我们将从在pom.xml中声明必要的依赖关系开始：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.8.1</version>
</dependency>

```

[此页面](https://search.maven.org/classic/#search|gav|1|g%3A"redis.clients" AND a%3A"jedis")上提供了最新版本的库。

## 4.Redis安装

然后我们将安装并启动 Redis 的最新版本之一。对于本教程，我们运行的是最新的稳定版本 (3.2.1)，但任何 3.x 之后的版本都应该没问题。

有关适用于 Linux 和 Macintosh 的 Redis 的更多信息，请查看[此链接](http://redis.io/topics/quickstart)；它们的基本安装步骤非常相似。Windows 不受官方支持，但此[端口](https://github.com/MSOpenTech/redis)维护良好。

现在我们可以直接深入，并从我们的Java代码连接到它：

```java
Jedis jedis = new Jedis();
```

默认构造函数将工作得很好，除非我们在非默认端口或远程机器上启动服务，在这种情况下，我们可以通过将正确的值作为参数传递给构造函数来正确配置它。

## 5. Redis 数据结构

支持大多数本地操作命令，而且很方便，它们通常共享相同的方法名称。

### 5.1. 字符串

字符串是最基本的 Redis 值类型，当我们需要持久化简单的键值数据类型时很有用：

```java
jedis.set("events/city/rome", "32,15,223,828");
String cachedResponse = jedis.get("events/city/rome");
```

变量cachedResponse将保存值32,15,223,828。再加上我们稍后将讨论的过期支持，它可以作为闪电般快速且易于使用的缓存层来处理我们的 Web 应用程序接收到的 HTTP 请求，以及其他缓存要求。

### 5.2. 列表

Redis 列表只是按插入顺序排序的字符串列表。这使它们成为实现消息队列的理想工具，例如：

```java
jedis.lpush("queue#tasks", "firstTask");
jedis.lpush("queue#tasks", "secondTask");

String task = jedis.rpop("queue#tasks");
```

变量task将保存值firstTask。请记住，我们可以序列化任何对象并将其作为字符串持久化，因此队列中的消息可以在需要时携带更复杂的数据。

### 5.3. 套

Redis Sets 是一个无序的字符串集合，当我们想要排除重复的成员时，它会派上用场：

```java
jedis.sadd("nicknames", "nickname#1");
jedis.sadd("nicknames", "nickname#2");
jedis.sadd("nicknames", "nickname#1");

Set<String> nicknames = jedis.smembers("nicknames");
boolean exists = jedis.sismember("nicknames", "nickname#1");
```

Java Set昵称的大小为 2，因为忽略了第二次添加的昵称#1 。此外，exists变量的值为true。sismember方法使我们能够快速检查特定成员的存在。

### 5.4. 哈希值

Redis 哈希是字符串字段和字符串值之间的映射：

```java
jedis.hset("user#1", "name", "Peter");
jedis.hset("user#1", "job", "politician");
		
String name = jedis.hget("user#1", "name");
		
Map<String, String> fields = jedis.hgetAll("user#1");
String job = fields.get("job");
```

正如我们所看到的，当我们想要单独访问一个对象的属性时，哈希是一种非常方便的数据类型，因为我们不需要检索整个对象。

### 5.5. 排序集

Sorted Sets 就像一个 Set，其中每个成员都有一个关联的排名，用于对它们进行排序：

```java
Map<String, Double> scores = new HashMap<>();

scores.put("PlayerOne", 3000.0);
scores.put("PlayerTwo", 1500.0);
scores.put("PlayerThree", 8200.0);

scores.entrySet().forEach(playerScore -> {
    jedis.zadd(key, playerScore.getValue(), playerScore.getKey());
});
		
String player = jedis.zrevrange("ranking", 0, 1).iterator().next();
long rank = jedis.zrevrank("ranking", "PlayerOne");
```

变量player将保留PlayerThree值，因为我们正在检索前 1 名玩家，而他是得分最高的玩家。rank变量的值为 1，因为PlayerOne在排名中排名第二，排名是从零开始的。

## 6. 交易

事务保证原子性和线程安全操作，这意味着来自其他客户端的请求永远不会在 Redis 事务期间并发处理：

```java
String friendsPrefix = "friends#";
String userOneId = "4352523";
String userTwoId = "5552321";

Transaction t = jedis.multi();
t.sadd(friendsPrefix + userOneId, userTwoId);
t.sadd(friendsPrefix + userTwoId, userOneId);
t.exec();
```

我们甚至可以在实例化我们的交易之前通过“观察”它来使交易的成功取决于特定的键：

```java
jedis.watch("friends#deleted#" + userOneId);
```

如果该键的值在交易执行前发生变化，交易将无法成功完成。

## 7.流水线

当我们必须发送多个命令时，我们可以将它们打包在一个请求中，并通过使用管道来节省连接开销。它本质上是一种网络优化。只要操作相互独立，我们就可以利用这种技术：

```java
String userOneId = "4352523";
String userTwoId = "4849888";

Pipeline p = jedis.pipelined();
p.sadd("searched#" + userOneId, "paris");
p.zadd("ranking", 126, userOneId);
p.zadd("ranking", 325, userTwoId);
Response<Boolean> pipeExists = p.sismember("searched#" + userOneId, "paris");
Response<Set<String>> pipeRanking = p.zrange("ranking", 0, -1);
p.sync();

String exists = pipeExists.get();
Set<String> ranking = pipeRanking.get();
```

请注意，我们无法直接访问命令响应。相反，我们得到了一个Response实例，我们可以在管道同步后从中请求底层响应。

## 8. 发布/订阅

我们可以使用 Redis 消息代理功能在我们系统的不同组件之间发送消息。我们只需要确保订阅者和发布者线程不共享相同的 Jedis 连接。

### 8.1. 订户

我们可以订阅和收听发送到频道的消息：

```java
Jedis jSubscriber = new Jedis();
jSubscriber.subscribe(new JedisPubSub() {
    @Override
    public void onMessage(String channel, String message) {
        // handle message
    }
}, "channel");
```

订阅是一种阻塞方法；我们需要明确地取消订阅JedisPubSub。这里我们重写了onMessage方法，但还有许多更有[用的方法](http://javadox.com/redis.clients/jedis/2.8.0/redis/clients/jedis/JedisPubSub.html)可以重写。

### 8.2. 出版商

然后我们可以简单地从发布者的线程向同一个频道发送消息：

```java
Jedis jPublisher = new Jedis();
jPublisher.publish("channel", "test message");
```

## 9.连接池

重要的是要知道我们处理 Jedis 实例的方式是天真的。在现实场景中，我们不想在多线程环境中使用单个实例，因为单个实例是线程安全的。

幸运的是，我们可以轻松地创建一个 Redis 连接池，供我们按需重用。这个池是线程安全可靠的，只要我们在使用完资源后将资源返回到池中即可。

让我们创建JedisPool：

```java
final JedisPoolConfig poolConfig = buildPoolConfig();
JedisPool jedisPool = new JedisPool(poolConfig, "localhost");

private JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
}
```

由于池实例是线程安全的，我们可以将其静态存储在某个地方，但我们应该注意销毁池以避免在应用程序关闭时发生泄漏。

现在我们可以在需要时从应用程序的任何地方使用我们的池：

```java
try (Jedis jedis = jedisPool.getResource()) {
    // do operations with jedis resource
}
```

我们使用Javatry-with-resources 语句来避免必须手动关闭 Jedis 资源，但如果我们不能使用此语句，我们也可以在finally子句中手动关闭资源。

如果我们不想面对讨厌的多线程问题，那么使用我们在应用程序中描述的池很重要。我们还可以使用池配置参数使其适应我们系统的最佳设置。

## 10.Redis集群

此 Redis 实施提供了简单的可扩展性和高可用性。为了更加熟悉它，我们可以查看他们的[官方规范](http://redis.io/topics/cluster-spec)。我们不会介绍 Redis 集群设置，因为这有点超出本文的范围，但是当我们完成文档后，我们应该不会有任何问题。

一旦我们准备好了，我们就可以从我们的应用程序开始使用它了：

```java
try (JedisCluster jedisCluster = new JedisCluster(new HostAndPort("localhost", 6379))) {
    // use the jedisCluster resource as if it was a normal Jedis resource
} catch (IOException e) {}
```

我们只需要提供一个主实例的主机和端口详细信息，它将自动发现集群中的其余实例。

这当然是一个非常强大的功能，但它不是灵丹妙药。在使用 Redis Cluster 时，我们无法执行事务或使用管道，这是许多应用程序依赖于确保数据完整性的两个重要功能。

事务被禁用是因为在集群环境中，密钥将在多个实例中持久保存。对于涉及命令在不同实例中执行的操作，无法保证操作原子性和线程安全。

一些高级密钥创建策略将确保我们希望在同一实例中保留的数据将以这种方式保留。从理论上讲，这应该使我们能够使用 Redis 集群的底层 Jedis 实例之一成功执行事务。

不幸的是，我们目前无法使用 Jedis(Redis 本身就支持)找到某个特定键保存在哪个 Redis 实例中，因此我们不知道我们必须在哪个实例上执行事务操作。如果我们想了解更多信息，可以[在此处](https://groups.google.com/forum/#!topic/redis-db/4I0ELYnf3bk)获得更多信息。

## 11.总结

Jedis 已经提供了 Redis 的绝大部分功能，并且它的开发进展顺利。

它使我们能够轻松地将强大的内存存储引擎集成到我们的应用程序中。我们只是不能忘记设置连接池来避免线程安全问题。