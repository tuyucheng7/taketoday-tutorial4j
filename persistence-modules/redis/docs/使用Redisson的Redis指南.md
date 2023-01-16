## 1. 概述

Redisson 是Java的 Redis 客户端。在本文中，我们将探讨它的一些特性，并演示它如何促进构建分布式业务应用程序。

Redisson 构成了一个内存数据网格，提供由 Redis 支持的分布式Java对象和服务。其分布式内存数据模型允许跨应用程序和服务器共享域对象和服务。

在本文中，我们将了解如何设置 Redisson，了解它的运作方式，并探索 Redisson 的一些对象和服务。

## 2.Maven依赖

让我们开始通过将以下部分添加到我们的pom.xml来将Redisson导入我们的项目：

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.13.1</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.redisson" AND a%3A"redisson")找到此依赖项的最新版本。

## 三、配置

在我们开始之前，我们必须确保我们有最新版本的 Redis 设置和运行。如果你没有 Redis 而使用 Linux 或 Macintosh，则可以按照[此处](http://redis.io/topics/quickstart)的信息进行设置。如果你是 Windows 用户，你可以使用这个非官方[端口](https://github.com/MSOpenTech/redis)来设置 Redis 。

我们需要配置 Redisson 以连接到 Redis。Redisson 支持连接到以下 Redis 配置：

-   单节点
-   主从节点
-   哨兵节点
-   集群节点
-   节点

Redisson 支持用于集群和节点的 Amazon Web Services (AWS) ElastiCache 集群和 Azure Redis 缓存。

让我们连接到 Redis 的单个节点实例。此实例在默认端口 6379 上本地运行：

```java
RedissonClient client = Redisson.create();
```

你可以将不同的配置传递给Redisson对象的创建方法。这可能是让它连接到不同端口的配置，或者可能是连接到 Redis 集群的配置。此配置可以使用Java代码或从外部配置文件加载。

### 3.1. Java配置

让我们在Java代码中配置 Redisson：

```java
Config config = new Config();
config.useSingleServer()
  .setAddress("redis://127.0.0.1:6379");

RedissonClient client = Redisson.create(config);
```

我们在Config对象的实例中指定 Redisson 配置，然后将其传递给create方法。上面，我们向 Redisson 指定了我们要连接到 Redis 的单节点实例。为此，我们使用了Config对象的useSingleServer方法。这将返回对SingleServerConfig对象的引用。

SingleServerConfig对象具有 Redisson 用于连接到 Redis 的单个节点实例的设置。在这里，我们使用它的setAddress方法来配置地址设置。这设置了我们要连接的节点的地址。其他一些设置包括retryAttempts、connectionTimeout和clientName。这些设置是使用它们相应的设置方法配置的。

我们可以使用Config对象的以下方法以类似的方式为不同的 Redis 配置配置 Redisson ：

-   useSingleServer——用于单节点实例。[在此处](https://github.com/redisson/redisson/wiki/2.-Configuration#261-single-instance-settings)获取单节点设置
-   useMasterSlaveServers——用于主节点和从节点。[在这里](https://github.com/redisson/redisson/wiki/2.-Configuration#281-master-slave-settings)获取主从节点设置
-   useSentinelServers——用于哨兵节点。[在此处](https://github.com/redisson/redisson/wiki/2.-Configuration#271-sentinel-settings)获取哨兵节点设置
-   useClusterServers – 用于集群节点。[在此处](https://github.com/redisson/redisson/wiki/2.-Configuration#241-cluster-settings)获取集群节点设置
-   useReplicatedServers——用于节点。[在此处](https://github.com/redisson/redisson/wiki/2.-Configuration#251-replicated-settings)获取的节点设置

### 3.2. 文件配置

Redisson 可以从外部 JSON 或 YAML文件加载配置：

```java
Config config = Config.fromJSON(new File("singleNodeConfig.json"));  
RedissonClient client = Redisson.create(config);
```

Config对象的fromJSON方法可以从字符串、文件、输入流或 URL 加载配置。

以下是singleNodeConfig.json文件中的示例配置：

```javascript
{
    "singleServerConfig": {
        "idleConnectionTimeout": 10000,
        "connectTimeout": 10000,
        "timeout": 3000,
        "retryAttempts": 3,
        "retryInterval": 1500,
        "password": null,
        "subscriptionsPerConnection": 5,
        "clientName": null,
        "address": "redis://127.0.0.1:6379",
        "subscriptionConnectionMinimumIdleSize": 1,
        "subscriptionConnectionPoolSize": 50,
        "connectionMinimumIdleSize": 10,
        "connectionPoolSize": 64,
        "database": 0,
        "dnsMonitoringInterval": 5000
    },
    "threads": 0,
    "nettyThreads": 0,
    "codec": null
}
```

这是一个相应的 YAML 配置文件：

```javascript
singleServerConfig:
    idleConnectionTimeout: 10000
    connectTimeout: 10000
    timeout: 3000
    retryAttempts: 3
    retryInterval: 1500
    password: null
    subscriptionsPerConnection: 5
    clientName: null
    address: "redis://127.0.0.1:6379"
    subscriptionConnectionMinimumIdleSize: 1
    subscriptionConnectionPoolSize: 50
    connectionMinimumIdleSize: 10
    connectionPoolSize: 64
    database: 0
    dnsMonitoringInterval: 5000
threads: 0
nettyThreads: 0
codec: !<org.redisson.codec.JsonJacksonCodec> {}

```

我们可以使用该配置特有的设置，以类似的方式从文件配置其他 Redis 配置。供你参考，以下是它们的 JSON 和 YAML 文件格式：

-   单节点 –[格式](https://github.com/redisson/redisson/wiki/2.-Configuration#262-single-instance-json-and-yaml-config-format)
-   Master with slave nodes –[格式](https://github.com/redisson/redisson/wiki/2.-Configuration#282-master-slave-json-and-yaml-config-format)
-   哨兵节点——[格式](https://github.com/redisson/redisson/wiki/2.-Configuration#272-sentinel-json-and-yaml-config-format)
-   集群节点 -[格式](https://github.com/redisson/redisson/wiki/2.-Configuration#242-cluster-json-and-yaml-config-format)
-   节点 -[格式](https://github.com/redisson/redisson/wiki/2.-Configuration#252-replicated-json-and-yaml-config-format)

要将Java配置保存为 JSON 或 YAML 格式，我们可以使用Config对象的toJSON或toYAML方法：

```java
Config config = new Config();
// ... we configure multiple settings here in Java
String jsonFormat = config.toJSON();
String yamlFormat = config.toYAML();
```

知道了如何配置Redisson之后，我们来看看Redisson是如何执行操作的。

## 4.操作

Redisson 支持同步、异步和反应式接口。这些接口上的操作是线程安全的。

RedissonClient生成的所有实体(对象、集合、锁和服务)都具有同步和异步方法。同步方法具有异步变体。这些方法通常具有与其同步变体相同的方法名称并附加“Async”。让我们看一下RAtomicLong对象的一个同步方法：

```java
RedissonClient client = Redisson.create();
RAtomicLong myLong = client.getAtomicLong('myLong');

```

同步compareAndSet方法的异步变体是：

```java
RFuture<Boolean> isSet = myLong.compareAndSetAsync(6, 27);
```

该方法的异步变体返回一个RFuture对象。我们可以在此对象上设置侦听器以在结果可用时取回结果：

```java
isSet.handle((result, exception) -> {
    // handle the result or exception here.
});
```

要生成反应对象，我们需要使用RedissonReactiveClient：

```java
RedissonReactiveClient client = Redisson.createReactive();
RAtomicLongReactive myLong = client.getAtomicLong("myLong");

Publisher<Boolean> isSetPublisher = myLong.compareAndSet(5, 28);
```

此方法基于Java9 的[Reactive Streams](http://www.reactive-streams.org/) Standard 返回反应对象。

让我们探索一下 Redisson 提供的一些分布式对象。

## 5.对象

Redisson 对象的单个实例被序列化并存储在支持 Redisson 的任何可用 Redis 节点中。这些对象可以分布在跨多个节点的集群中，并且可以由单个应用程序或多个应用程序/服务器访问。

这些分布式对象遵循java.util.concurrent.atomic 包中的规范。它们支持对存储在 Redis 中的对象进行无锁、线程安全和原子操作。确保应用程序/服务器之间的数据一致性，因为值不会在另一个应用程序读取对象时更新。

Redisson 对象绑定到 Redis 键。我们可以通过RKeys接口来管理这些密钥。然后，我们使用这些键访问我们的 Redisson 对象。

我们可以使用多种选项来获取 Redis 密钥。

我们可以简单地获取所有密钥：

```java
RKeys keys = client.getKeys();
```

或者，我们可以只提取名称：

```java
Iterable<String> allKeys = keys.getKeys();
```

最后，我们能够获得符合模式的键：

```java
Iterable<String> keysByPattern = keys.getKeysByPattern('key')
```

RKeys 接口还允许删除键、按模式删除键以及我们可以用来管理我们的键和对象的其他有用的基于键的操作。

Redisson提供的分布式对象包括：

-   对象持有者
-   BinaryStreamHolder
-   地理空间持有人
-   位集
-   原子长
-   原子双
-   话题
-   布隆过滤器
-   超级日志日志

我们来看看其中的三个对象：ObjectHolder、AtomicLong和Topic。

### 5.1. 对象持有人

由RBucket类表示，这个对象可以容纳任何类型的对象。此对象的最大大小为 512MB：

```java
RBucket<Ledger> bucket = client.getBucket("ledger");
bucket.set(new Ledger());
Ledger ledger = bucket.get();
```

RBucket对象可以对其持有的对象执行原子操作，例如compareAndSet 和 getAndSet。

### 5.2. 原子长

该对象由RAtomicLong类表示，与java.util.concurrent.atomic.AtomicLong类非常相似，表示可以自动更新的long值：

```java
RAtomicLong atomicLong = client.getAtomicLong("myAtomicLong");
atomicLong.set(5);
atomicLong.incrementAndGet();
```

### 5.3. 话题

Topic对象支持Redis的“发布和订阅”机制。收听已发布的消息：

```java
RTopic subscribeTopic = client.getTopic("baeldung");
subscribeTopic.addListener(CustomMessage.class,
  (channel, customMessage) -> future.complete(customMessage.getMessage()));
```

上面，主题被注册为收听来自“baeldung”频道的消息。然后，我们向主题添加一个侦听器以处理来自该频道的传入消息。我们可以向一个频道添加多个侦听器。

让我们将消息发布到“baeldung”频道：

```java
RTopic publishTopic = client.getTopic("baeldung");
long clientsReceivedMessage
  = publishTopic.publish(new CustomMessage("This is a message"));
```

这可以从另一个应用程序或服务器发布。CustomMessage对象将由侦听器接收并按照 onMessage 方法中的定义进行处理。

[我们可以在这里](https://github.com/redisson/redisson/wiki/6.-distributed-objects)了解更多关于其他 Redisson 对象的信息。

## 6.收藏品

我们以与处理对象相同的方式处理 Redisson 集合。

Redisson提供的分布式集合包括：

-   地图
-   多图
-   放
-   排序集
-   评分排序集
-   Lex排序集
-   列表
-   队列
-   Deque
-   阻塞队列
-   有界阻塞队列
-   阻塞双端队列
-   阻塞公平队列
-   延迟队列
-   优先队列
-   优先队列

让我们来看看其中的三个集合：Map、Set和List。

### 6.1. 地图

基于 Redisson 的地图实现了java.util.concurrent.ConcurrentMap和java.util.Map接口。Redisson 有四种地图实现。它们是RMap、RMapCache、RLocalCachedMap和RClusteredMap。

让我们用 Redisson 创建一个地图：

```java
RMap<String, Ledger> map = client.getMap("ledger");
Ledger newLedger = map.put("123", new Ledger());map
```

RMapCache支持映射条目逐出。RLocalCachedMap允许映射条目的本地缓存。RClusteredMap允许将来自单个映射的数据拆分到 Redis 集群主节点。

[我们可以在这里](https://github.com/redisson/redisson/wiki/7.-distributed-collections#71-map)了解更多关于 Redisson 地图的信息。

### 6.2. 放

基于 Redisson 的Set实现了java.util.Set接口。

Redisson 具有三个Set实现，即 RSet 、 RSetCache和RClusteredSet，它们的功能与对应的地图类似。

让我们用 Redisson创建一个集合：

```java
RSet<Ledger> ledgerSet = client.getSet("ledgerSet");
ledgerSet.add(new Ledger());
```

[我们可以在这里](https://github.com/redisson/redisson/wiki/7.-distributed-collections#71-map)了解更多关于 Redisson 集合的信息。

### 6.3. 列表

基于 Redisson 的列表实现了java.util.List接口。

让我们用 Redisson创建一个列表：

```java
RList<Ledger> ledgerList = client.getList("ledgerList");
ledgerList.add(new Ledger());
```

[我们可以在这里](https://github.com/redisson/redisson/wiki/7.-distributed-collections)了解更多关于其他 Redisson 集合的信息。

## 7.锁和同步器

Redisson 的分布式锁允许跨应用程序/服务器的线程同步。Redisson 的锁和同步器列表包括：

-   锁
-   公平锁
-   多重锁
-   读写锁
-   信号
-   PermitExpirable 信号量
-   倒数锁存器

让我们来看看Lock和MultiLock。

### 7.1. 锁

Redisson的Lock实现了java.util.concurrent.locks.Lock接口。

让我们实现一个锁，由RLock类表示：

```java
RLock lock = client.getLock("lock");
lock.lock();
// perform some long operations...
lock.unlock();
```

### 7.2. 多重锁

Redisson 的RedissonMultiLock将多个RLock对象分组，并将它们视为单个锁：

```java
RLock lock1 = clientInstance1.getLock("lock1");
RLock lock2 = clientInstance2.getLock("lock2");
RLock lock3 = clientInstance3.getLock("lock3");

RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
lock.lock();
// perform long running operation...
lock.unlock();
```

[我们可以在这里](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers)了解更多关于其他锁具的信息。

## 八、服务

Redisson 公开了 4 种类型的分布式服务。它们是：Remote Service、Live Object Service、Executor Service和Scheduled Executor Service。让我们看看远程服务和活动对象服务。

### 8.1. 远程服务

此服务提供由 Redis 促进的Java远程方法调用。Redisson 远程服务由服务器端(工作实例)和客户端实现组成。服务器端实现执行客户端调用的远程方法。来自远程服务的调用可以是同步的或异步的。

服务器端注册一个接口用于远程调用：

```java
RRemoteService remoteService = client.getRemoteService();
LedgerServiceImpl ledgerServiceImpl = new LedgerServiceImpl();

remoteService.register(LedgerServiceInterface.class, ledgerServiceImpl);
```

客户端调用已注册远程接口的方法：

```java
RRemoteService remoteService = client.getRemoteService();
LedgerServiceInterface ledgerService
  = remoteService.get(LedgerServiceInterface.class);

List<String> entries = ledgerService.getEntries(10);
```

[我们可以在这里](https://github.com/redisson/redisson/wiki/9.-distributed-services#91-remote-service)了解更多关于远程服务的信息。

### 8.2. 活动对象服务

Redisson Live Objects 将只能从单个 JVM 访问的标准Java对象的概念扩展为可以在不同机器的不同 JVM 之间共享的增强型Java对象。这是通过将对象的字段映射到 Redis 哈希来实现的。此映射是通过运行时构造的代理类进行的。字段 getter 和 setter 映射到 Redis hget/hset 命令。

由于 Redis 的单线程特性，Redisson Live Objects 支持原子字段访问。

创建活动对象很简单：

```java
@REntity
public class LedgerLiveObject {
    @RId
    private String name;

    // getters and setters...
}
```

我们用@REntity注解我们的类，用@RId注解一个唯一的或标识字段。完成此操作后，我们可以在我们的应用程序中使用我们的活动对象：

```java
RLiveObjectService service = client.getLiveObjectService();

LedgerLiveObject ledger = new LedgerLiveObject();
ledger.setName("ledger1");

ledger = service.persist(ledger);
```

我们使用new关键字创建像标准Java对象一样的活动对象。然后，我们使用RLiveObjectService的一个实例，使用它的persist方法将对象保存到 Redis 。

如果该对象之前已经被持久化到 Redis，我们可以检索该对象：

```java
LedgerLiveObject returnLedger
  = service.get(LedgerLiveObject.class, "ledger1");
```

我们使用RLiveObjectService使用带有@RId注解的字段来获取我们的活动对象。

[在这里](https://github.com/redisson/redisson/wiki/9.-distributed-services#92-live-object-service)我们可以找到更多关于 Redisson Live Objects 的信息，其他 Redisson 服务也在[这里描述。](https://github.com/redisson/redisson/wiki/9.-distributed-services)

## 9.流水线

Redisson 支持流水线。多个操作可以作为单个原子操作进行批处理。RBatch类促进了这一点。多个命令在执行之前针对RBatch对象实例进行聚合：

```java
RBatch batch = client.createBatch();
batch.getMap("ledgerMap").fastPutAsync("1", "2");
batch.getMap("ledgerMap").putAsync("2", "5");

BatchResult<?> batchResult = batch.execute();
```

## 10. 脚本

Redisson 支持 LUA 脚本。我们可以针对 Redis 执行 LUA 脚本：

```java
client.getBucket("foo").set("bar");
String result = client.getScript().eval(Mode.READ_ONLY,
  "return redis.call('get', 'foo')", RScript.ReturnType.VALUE);
```

## 11.低级客户端

我们可能想要执行 Redisson 尚不支持的 Redis 操作。Redisson 提供了一个允许执行本机 Redis 命令的低级客户端：

```java
RedisClientConfig redisClientConfig = new RedisClientConfig();
redisClientConfig.setAddress("localhost", 6379);

RedisClient client = RedisClient.create(redisClientConfig);

RedisConnection conn = client.connect();
conn.sync(StringCodec.INSTANCE, RedisCommands.SET, "test", 0);

conn.closeAsync();
client.shutdown();
```

低级客户端也支持异步操作。

## 12.总结

本文展示了 Redisson 以及使其成为开发分布式应用程序的理想选择的一些特性。我们探索了它的分布式对象、集合、锁和服务。我们还探讨了它的一些其他功能，例如流水线、脚本及其低级客户端。

Redisson 还提供与其他框架的集成，例如 JCache API、Spring Cache、Hibernate Cache 和 Spring Sessions。[我们可以在这里](https://github.com/redisson/redisson/wiki/14.-Integration-with-frameworks)了解更多关于它与其他框架的集成。