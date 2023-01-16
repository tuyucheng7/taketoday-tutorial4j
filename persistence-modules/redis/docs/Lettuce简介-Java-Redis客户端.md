## 1. 概述

本文是对[Redis](http://redis.io/)Java客户端[Lettuce](https://lettuce.io/)的介绍。

Redis 是一种内存中的键值存储，可用作数据库、缓存或消息代理。使用对 Redis 内存数据结构中的键进行操作的[命令](https://redis.io/commands)来添加、查询、修改和删除数据。

Lettuce 支持使用完整的 Redis API 进行同步和异步通信，包括其数据结构、发布/订阅消息传递和高可用性服务器连接。

## 2. 为什么选择生菜？

我们[在之前的一篇文章中介绍了 Jedis。](https://www.baeldung.com/jedis-java-redis-client-library)是什么让生菜与众不同？

最显着的区别是它通过Java8 的CompletionStage接口提供的异步支持和对 Reactive Streams 的支持。正如我们将在下面看到的，Lettuce 提供了一个自然的接口，用于从 Redis 数据库服务器发出异步请求和创建流。

它还使用Netty与服务器通信。这使得 API 变得“更重”，但也使其更适合与多个线程共享连接。

## 3.设置

### 3.1. 依赖性

让我们首先在pom.xml中声明我们需要的唯一依赖项：

```plaintext
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>5.0.1.RELEASE</version>
</dependency>

```

[可以在Github 存储库](https://github.com/lettuce-io/lettuce-core)或[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"io.lettuce" AND a%3A"lettuce-core")上检查该库的最新版本。

### 3.2. Redis安装

我们需要安装并运行至少一个 Redis 实例，如果我们希望测试集群或哨兵模式(尽管哨兵模式需要三台服务器才能正常运行)，则需要两个实例。在本文中，我们使用的是 4.0.x –目前最新的稳定版本。

[可以在此处](https://redis.io/topics/quickstart)找到有关开始使用 Redis 的更多信息，包括适用于 Linux 和 MacOS 的下载。

Redis 不正式支持 Windows，但这里有一个服务器的[端口](https://github.com/MicrosoftArchive/redis)。我们还可以在[Docker](https://hub.docker.com/_/redis/)中运行 Redis，这是 Windows 10 的更好替代方案，也是一种快速启动和运行的方法。

## 4.连接

### 4.1. 连接到服务器

连接到 Redis 包括四个步骤：

1.  创建 Redis URI
2.  使用 URI 连接到RedisClient
3.  打开 Redis 连接
4.  生成一组RedisCommands

让我们看看实现：

```java
RedisClient redisClient = RedisClient
  .create("redis://password@localhost:6379/");
StatefulRedisConnection<String, String> connection
 = redisClient.connect();
```

StatefulRedisConnection就是它听起来的样子；到 Redis 服务器的线程安全连接，它将保持与服务器的连接并在需要时重新连接。建立连接后，我们可以使用它来同步或异步执行 Redis 命令。

RedisClient使用大量系统资源，因为它拥有用于与 Redis 服务器通信的 Netty 资源。需要多个连接的应用程序应该使用单个RedisClient。

### 4.2. Redis URI

我们通过将 URI 传递给静态工厂方法来创建RedisClient 。

Lettuce 利用 Redis URI 的自定义语法。这是架构：

```plaintext
redis :// [password@] host [: port] [/ database]
  [? [timeout=timeout[d|h|m|s|ms|us|ns]]
  [&_database=database_]]

```

有四种 URI 方案：

-   redis – 一个独立的 Redis 服务器
-   rediss – 通过 SSL 连接的独立 Redis 服务器
-   redis-socket – 通过 Unix 域套接字的独立 Redis 服务器
-   redis-sentinel – Redis Sentinel 服务器

Redis 数据库实例可以指定为 URL 路径的一部分或附加参数。如果两者都提供，则该参数具有更高的优先级。

在上面的示例中，我们使用的是String表示形式。Lettuce 还有一个用于建立连接的RedisURI类。它提供了Builder模式：

```java
RedisURI.Builder
  .redis("localhost", 6379).auth("password")
  .database(1).build();

```

和一个构造函数：

```java
new RedisURI("localhost", 6379, 60, TimeUnit.SECONDS);

```

### 4.3. 同步命令

与Jedis类似，Lettuce以方法的形式提供了完整的Redis命令集。

但是，Lettuce 实现了同步和异步版本。我们将简要介绍同步版本，然后在本教程的其余部分使用异步实现。

创建连接后，我们使用它来创建命令集：

```java
RedisCommands<String, String> syncCommands = connection.sync();

```

现在我们有一个与 Redis 通信的直观界面。

我们可以设置和获取字符串值：

```java
syncCommands.set("key", "Hello, Redis!");

String value = syncommands.get(“key”);

```

我们可以使用哈希：

```java
syncCommands.hset("recordName", "FirstName", "John");
syncCommands.hset("recordName", "LastName", "Smith");
Map<String, String> record = syncCommands.hgetall("recordName");

```

我们将在本文后面介绍更多 Redis。

Lettuce 同步 API 使用异步 API。阻塞是在命令级别为我们完成的。这意味着多个客户端可以共享一个同步连接。

### 4.4. 异步命令

让我们看一下异步命令：

```java
RedisAsyncCommands<String, String> asyncCommands = connection.async();

```

我们从连接中检索一组RedisAsyncCommand，类似于我们检索同步集的方式。这些命令返回一个RedisFuture(在内部是一个CompletableFuture)：

```java
RedisFuture<String> result = asyncCommands.get("key");

```

可以在[此处找到使用](https://www.baeldung.com/java-completablefuture)CompletableFuture的指南。

### 4.5. 反应性API

最后，让我们看看如何使用非阻塞反应式 API：

```java
RedisStringReactiveCommands<String, String> reactiveCommands = connection.reactive();

```

这些命令从[Project Reactor返回包含在](https://github.com/reactor/reactor-core)Mono或Flux中的结果[。](https://github.com/reactor/reactor-core)

可以在[此处找到使用 Project Reactor 的指南。](https://www.baeldung.com/reactor-core)

## 5. Redis 数据结构

上面简单的看了strings和hashes，再看看Lettuce是如何实现Redis的其余数据结构的。正如我们所料，每个 Redis 命令都有一个类似命名的方法。

### 5.1. 列表

列表是保留插入顺序的字符串列表。从任一端插入或检索值：

```java
asyncCommands.lpush("tasks", "firstTask");
asyncCommands.lpush("tasks", "secondTask");
RedisFuture<String> redisFuture = asyncCommands.rpop("tasks");

String nextTask = redisFuture.get();

```

在此示例中，nextTask等于“ firstTask ”。Lpush将值推入列表的头部，然后rpop从列表的末尾弹出值。

我们也可以从另一端弹出元素：

```java
asyncCommands.del("tasks");
asyncCommands.lpush("tasks", "firstTask");
asyncCommands.lpush("tasks", "secondTask");
redisFuture = asyncCommands.lpop("tasks");

String nextTask = redisFuture.get();

```

我们通过使用del删除列表来开始第二个示例。然后我们再次插入相同的值，但是我们使用lpop从列表的头部弹出值，所以nextTask包含“ secondTask ”文本。

### 5.2. 套

Redis Sets 是类似于JavaSets的无序字符串集合；没有重复的元素：

```java
asyncCommands.sadd("pets", "dog");
asyncCommands.sadd("pets", "cat");
asyncCommands.sadd("pets", "cat");
 
RedisFuture<Set<String>> pets = asyncCommands.smembers("nicknames");
RedisFuture<Boolean> exists = asyncCommands.sismember("pets", "dog");

```

当我们将 Redis 集检索为Set时，大小为二，因为忽略了重复的“cat” 。当我们用sismember查询 Redis 是否存在“dog”时，响应为true。

### 5.3. 哈希值

我们之前简要地看了一个哈希的例子。他们值得快速解释一下。

Redis 哈希是具有字符串字段和值的记录。每条记录在主索引中也有一个键：

```java
asyncCommands.hset("recordName", "FirstName", "John");
asyncCommands.hset("recordName", "LastName", "Smith");

RedisFuture<String> lastName 
  = syncCommands.hget("recordName", "LastName");
RedisFuture<Map<String, String>> record 
  = syncCommands.hgetall("recordName");

```

我们使用hset向散列中添加字段，传入散列名称、字段名称和一个值。

然后，我们使用hget、记录名称和字段检索单个值。最后，我们使用hgetall获取整个记录作为散列。

### 5.4. 排序集

Sorted Sets 包含值和排序依据。秩是 64 位浮点值。

项目添加了一个等级，并在一个范围内检索：

```java
asyncCommands.zadd("sortedset", 1, "one");
asyncCommands.zadd("sortedset", 4, "zero");
asyncCommands.zadd("sortedset", 2, "two");

RedisFuture<List<String>> valuesForward = asyncCommands.zrange(key, 0, 3);
RedisFuture<List<String>> valuesReverse = asyncCommands.zrevrange(key, 0, 3);

```

zadd的第二个参数是等级。我们通过zrange升序检索范围， zrevrange降序检索范围。

我们添加了等级为 4 的“零”，因此它将出现在valuesForward的末尾和valuesReverse的开头。

## 6. 交易

事务允许在单个原子步骤中执行一组命令。这些命令保证按顺序和独占执行。在事务完成之前，不会执行来自另一个用户的命令。

要么执行所有命令，要么都不执行。如果其中一个失败，Redis 将不会执行回滚。一旦exec()被调用，所有的命令都会按照指定的顺序执行。

让我们看一个例子：

```java
asyncCommands.multi();
    
RedisFuture<String> result1 = asyncCommands.set("key1", "value1");
RedisFuture<String> result2 = asyncCommands.set("key2", "value2");
RedisFuture<String> result3 = asyncCommands.set("key3", "value3");

RedisFuture<TransactionResult> execResult = asyncCommands.exec();

TransactionResult transactionResult = execResult.get();

String firstResult = transactionResult.get(0);
String secondResult = transactionResult.get(0);
String thirdResult = transactionResult.get(0);

```

对multi的调用启动事务。事务启动后，直到调用exec()后才会执行后续命令。

在同步模式下，命令返回null。在异步模式下，命令返回RedisFuture。Exec返回一个包含响应列表的TransactionResult 。

由于RedisFutures也接收它们的结果，异步 API 客户端在两个地方接收交易结果。

## 7.批处理

在正常情况下，Lettuce 会在 API 客户端调用命令时立即执行命令。

这是大多数普通应用程序想要的，尤其是当它们依赖于串行接收命令结果时。

但是，如果应用程序不需要立即获得结果或批量上传大量数据，则此行为效率不高。

异步应用程序可以覆盖此行为：

```java
commands.setAutoFlushCommands(false);

List<RedisFuture<?>> futures = new ArrayList<>();
for (int i = 0; i < iterations; i++) {
    futures.add(commands.set("key-" + i, "value-" + i);
}
commands.flushCommands();

boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS,
  futures.toArray(new RedisFuture[0]));


```

当 setAutoFlushCommands 设置为false时，应用程序必须手动调用flushCommands。在此示例中，我们将多个set命令排队，然后刷新通道。AwaitAll等待所有RedisFutures完成。

此状态是在每个连接的基础上设置的，并影响使用该连接的所有线程。此功能不适用于同步命令。

## 8. 发布/订阅

Redis 提供了一个简单的发布/订阅消息系统。订阅者使用subscribe命令消费来自频道的消息。消息不会持久化；只有当用户订阅了某个频道时，它们才会交付给用户。

Redis 使用 pub/sub 系统来通知 Redis 数据集，使客户端能够接收有关设置、删除、过期等键的事件。

有关详细信息，请参阅[此处](https://redis.io/topics/notifications)的文档。

### 8.1. 订户

RedisPubSubListener接收发布/订阅消息。这个接口定义了几个方法，但是我们在这里只展示接收消息的方法：

```java
public class Listener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String message) {
        log.debug("Got {} on channel {}",  message, channel);
        message = new String(s2);
    }
}

```

我们使用RedisClient连接发布/订阅频道并安装监听器：

```java
StatefulRedisPubSubConnection<String, String> connection
 = client.connectPubSub();
connection.addListener(new Listener())

RedisPubSubAsyncCommands<String, String> async
 = connection.async();
async.subscribe("channel");

```

安装侦听器后，我们检索一组RedisPubSubAsyncCommands并订阅一个频道。

### 8.2. 出版商

发布只是连接 Pub/Sub 通道并检索命令的问题：

```java
StatefulRedisPubSubConnection<String, String> connection 
  = client.connectPubSub();

RedisPubSubAsyncCommands<String, String> async 
  = connection.async();
async.publish("channel", "Hello, Redis!");

```

发布需要一个频道和一条消息。

### 8.3. 反应式订阅

Lettuce 还提供了一个用于订阅发布/订阅消息的反应式接口：

```java
StatefulRedisPubSubConnection<String, String> connection = client
  .connectPubSub();

RedisPubSubAsyncCommands<String, String> reactive = connection
  .reactive();

reactive.observeChannels().subscribe(message -> {
    log.debug("Got {} on channel {}",  message, channel);
    message = new String(s2);
});
reactive.subscribe("channel").subscribe();

```

observeChannels返回的Flux接收所有频道的消息，但由于这是一个流，过滤很容易做到。

## 9.高可用性

Redis 为高可用性和可伸缩性提供了多种选择。完全理解需要了解 Redis 服务器配置，但我们将简要概述 Lettuce 如何支持它们。

### 9.1. 主从

Redis 服务器以主/从配置进行自我。主服务器向从属服务器发送命令流，将主缓存到从属服务器。Redis 不支持双向，所以从属是只读的。

Lettuce 可以连接到 Master/Slave 系统，查询它们的拓扑结构，然后选择 slaves 进行读取操作，这样可以提高吞吐量：

```java
RedisClient redisClient = RedisClient.create();

StatefulRedisMasterSlaveConnection<String, String> connection
 = MasterSlave.connect(redisClient, 
   new Utf8StringCodec(), RedisURI.create("redis://localhost"));
 
connection.setReadFrom(ReadFrom.SLAVE);

```

### 9.2. 哨兵

Redis Sentinel 监控主实例和从实例，并在发生主故障转移时协调故障转移到从实例。

Lettuce 可以连接到 Sentinel，用它来发现当前 master 的地址，然后返回一个连接给它。

为此，我们构建了一个不同的RedisURI并将我们的RedisClient与其连接：

```java
RedisURI redisUri = RedisURI.Builder
  .sentinel("sentinelhost1", "clustername")
  .withSentinel("sentinelhost2").build();
RedisClient client = new RedisClient(redisUri);

RedisConnection<String, String> connection = client.connect();

```

我们使用第一个 Sentinel 的主机名(或地址)和集群名称构建 URI，后跟第二个 Sentinel 地址。当我们连接到 Sentinel 时，Lettuce 会查询它的拓扑结构并为我们返回到当前主服务器的连接。

完整的文档可[在此处获得。](https://redis.io/topics/sentinel)

### 9.3. 集群

Redis Cluster 使用分布式配置来提供高可用性和高吞吐量。

跨多达 1000 个节点的集群分片键，因此事务在集群中不可用：

```java
RedisURI redisUri = RedisURI.Builder.redis("localhost")
  .withPassword("authentication").build();
RedisClusterClient clusterClient = RedisClusterClient
  .create(rediUri);
StatefulRedisClusterConnection<String, String> connection
 = clusterClient.connect();
RedisAdvancedClusterCommands<String, String> syncCommands = connection
  .sync();

```

RedisAdvancedClusterCommands保存集群支持的一组 Redis 命令，将它们路由到持有密钥的实例。

完整的规范可[在此处](https://redis.io/topics/cluster-spec)获得。

## 10.总结

在本教程中，我们研究了如何使用 Lettuce 从我们的应用程序中连接和查询 Redis 服务器。

Lettuce 支持完整的 Redis 功能集，并具有完全线程安全的异步接口。它还广泛使用了Java8 的CompletionStage接口，使应用程序能够细粒度地控制它们接收数据的方式。