## 1. 简介

[Apache Curator](https://curator.apache.org/)是 Apache Zookeeper 的Java客户端，[Apache Zookeeper](https://zookeeper.apache.org/)是分布式应用程序的流行协调服务。

在本教程中，我们将介绍 Curator 提供的一些最相关的功能：

-   连接管理——管理连接和重试策略
-   异步——通过添加异步功能和使用Java8 lambda 来增强现有客户端
-   配置管理——对系统进行集中配置
-   强类型模型——使用类型化模型
-   秘诀——实施领导者选举、分布式锁或计数器

## 2.先决条件

首先，建议快速浏览一下[Apache Zookeeper](https://zookeeper.apache.org/)及其功能。

对于本教程，我们假设已经有一个独立的 Zookeeper 实例在127.0.0.1:2181上运行；如果你刚刚开始，[这里有关于如何安装和运行它的说明。](https://zookeeper.apache.org/doc/current/zookeeperStarted.html)

首先，我们需要将[curator-x-async](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.curator" AND a%3A"curator-x-async")依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-x-async</artifactId>
    <version>4.0.1</version>
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

最新版本的 Apache Curator 4.XX 与目前仍处于测试阶段的 Zookeeper 3.5.X 存在硬依赖关系。

因此，在本文中，我们将改用当前最新的稳定[版 Zookeeper 3.4.11](https://zookeeper.apache.org/doc/r3.4.11/index.html)。

所以我们需要排除 Zookeeper 依赖并将[Zookeeper 版本的依赖](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.zookeeper" AND a%3A"zookeeper")添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.11</version>
</dependency>
```

有关兼容性的更多信息，请参阅[此链接](https://curator.apache.org/zk-compatibility-34.html)。

## 3.连接管理

Apache Curator 的基本用例是连接到正在运行的 Apache Zookeeper 实例。

该工具提供了一个工厂来使用重试策略建立与 Zookeeper 的连接：

```java
int sleepMsBetweenRetries = 100;
int maxRetries = 3;
RetryPolicy retryPolicy = new RetryNTimes(
  maxRetries, sleepMsBetweenRetries);

CuratorFramework client = CuratorFrameworkFactory
  .newClient("127.0.0.1:2181", retryPolicy);
client.start();
 
assertThat(client.checkExists().forPath("/")).isNotNull();
```

在这个快速示例中，我们将重试 3 次，并在重试之间等待 100 毫秒，以防出现连接问题。

使用CuratorFramework客户端连接到 Zookeeper后，我们现在可以浏览路径、获取/设置数据并与服务器交互。

## 4.异步

Curator Async 模块包装了上述CuratorFramework客户端，以使用[CompletionStageJava8 API](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletionStage.html)提供非阻塞功能。

让我们看看前面的示例如何使用异步包装器：

```java
int sleepMsBetweenRetries = 100;
int maxRetries = 3;
RetryPolicy retryPolicy 
  = new RetryNTimes(maxRetries, sleepMsBetweenRetries);

CuratorFramework client = CuratorFrameworkFactory
  .newClient("127.0.0.1:2181", retryPolicy);

client.start();
AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);

AtomicBoolean exists = new AtomicBoolean(false);

async.checkExists()
  .forPath("/")
  .thenAcceptAsync(s -> exists.set(s != null));

await().until(() -> assertThat(exists.get()).isTrue());
```

现在，checkExists()操作以异步模式工作，不会阻塞主线程。我们也可以使用thenAcceptAsync()方法一个接一个地链接操作，该方法使用[CompletionStage API](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletionStage.html)。

## 5.配置管理

在分布式环境中，最常见的挑战之一是管理许多应用程序之间的共享配置。我们可以使用 Zookeeper 作为数据存储来保存我们的配置。

让我们看一个使用 Apache Curator 获取和设置数据的示例：

```java
CuratorFramework client = newClient();
client.start();
AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);
String key = getKey();
String expected = "my_value";

client.create().forPath(key);

async.setData()
  .forPath(key, expected.getBytes());

AtomicBoolean isEquals = new AtomicBoolean();
async.getData()
  .forPath(key)
  .thenAccept(data -> isEquals.set(new String(data).equals(expected)));

await().until(() -> assertThat(isEquals.get()).isTrue());
```

在这个例子中，我们创建节点路径，在 Zookeeper 中设置数据，然后我们恢复它检查值是否相同。key字段可以是像/config/dev/my_key这样的节点路径。

### 5.1. 守望者

Zookeeper 中另一个有趣的特性是监视键或节点的能力。它允许我们监听配置的变化并更新我们的应用程序而无需重新部署。

让我们看看上面的例子在使用观察者时是什么样子的：

```java
CuratorFramework client = newClient()
client.start();
AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);
String key = getKey();
String expected = "my_value";

async.create().forPath(key);

List<String> changes = new ArrayList<>();

async.watched()
  .getData()
  .forPath(key)
  .event()
  .thenAccept(watchedEvent -> {
    try {
        changes.add(new String(client.getData()
          .forPath(watchedEvent.getPath())));
    } catch (Exception e) {
        // fail ...
    }});

// Set data value for our key
async.setData()
  .forPath(key, expected.getBytes());

await()
  .until(() -> assertThat(changes.size()).isEqualTo(1));
```

我们配置观察者，设置数据，然后确认被观察的事件被触发。我们可以一次观察一个节点或一组节点。

## 6.强类型模型

Zookeeper 主要处理字节数组，因此我们需要对数据进行序列化和反序列化。这使我们可以灵活地处理任何可序列化的实例，但它可能很难维护。

为了提供帮助，Curator 添加了[类型化模型](https://curator.apache.org/curator-x-async/modeled.html)的概念，它委托序列化/反序列化并允许我们直接使用我们的类型。让我们看看它是如何工作的。

首先，我们需要一个序列化器框架。Curator 建议使用 Jackson 实现，所以让我们将[Jackson 依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

现在，让我们尝试保留我们的自定义类HostConfig：

```java
public class HostConfig {
    private String hostname;
    private int port;

    // getters and setters
}
```

我们需要提供从HostConfig类到路径的模型规范映射，并使用 Apache Curator 提供的建模框架包装器：

```java
ModelSpec<HostConfig> mySpec = ModelSpec.builder(
  ZPath.parseWithIds("/config/dev"), 
  JacksonModelSerializer.build(HostConfig.class))
  .build();

CuratorFramework client = newClient();
client.start();

AsyncCuratorFramework async 
  = AsyncCuratorFramework.wrap(client);
ModeledFramework<HostConfig> modeledClient 
  = ModeledFramework.wrap(async, mySpec);

modeledClient.set(new HostConfig("host-name", 8080));

modeledClient.read()
  .whenComplete((value, e) -> {
     if (e != null) {
          fail("Cannot read host config", e);
     } else {
          assertThat(value).isNotNull();
          assertThat(value.getHostname()).isEqualTo("host-name");
          assertThat(value.getPort()).isEqualTo(8080);
     }
   });
```

whenComplete ()方法在读取路径/config/dev时会返回Zookeeper中的HostConfig实例。

## 7.食谱

Zookeeper 提供[此指南](https://zookeeper.apache.org/doc/current/recipes.html)来实现高级解决方案或配方，例如领导者选举、分布式锁或共享计数器。

Apache Curator 为大多数这些食谱提供了一个实现。要查看完整列表，请访问[Curator Recipes 文档](https://curator.apache.org/curator-recipes/index.html)。

所有这些食谱都在一个单独的模块中可用：

```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.0.1</version>
</dependency>
```

让我们直接进入并通过一些简单的例子开始理解这些。

### 7.1. 领导人选举

在分布式环境中，我们可能需要一个主节点或领导节点来协调一项复杂的工作。

这就是Curator[中 Leader Election 配方的用法：](https://curator.apache.org/curator-recipes/leader-election.html)

```java
CuratorFramework client = newClient();
client.start();
LeaderSelector leaderSelector = new LeaderSelector(client, 
  "/mutex/select/leader/for/job/A", 
  new LeaderSelectorListener() {
      @Override
      public void stateChanged(
        CuratorFramework client, 
        ConnectionState newState) {
      }

      @Override
      public void takeLeadership(
        CuratorFramework client) throws Exception {
      }
  });

// join the members group
leaderSelector.start();

// wait until the job A is done among all members
leaderSelector.close();
```

当我们启动领导者选择器时，我们的节点会加入路径/mutex/select/leader/for/job/A中的成员组。一旦我们的节点成为领导者，就会调用takeLeadership方法，我们作为领导者可以恢复工作。

### 7.2. 共享锁

[共享锁配方](https://curator.apache.org/curator-recipes/shared-lock.html)是关于拥有一个完全分布式的锁：

```java
CuratorFramework client = newClient();
client.start();
InterProcessSemaphoreMutex sharedLock = new InterProcessSemaphoreMutex(
  client, "/mutex/process/A");

sharedLock.acquire();

// do process A

sharedLock.release();
```

当我们获取锁时，Zookeeper 确保没有其他应用程序同时获取同一个锁。

### 7.3. 柜台

[计数器配方](https://curator.apache.org/curator-recipes/shared-counter.html)协调所有客户端之间的共享整数：

```java
CuratorFramework client = newClient();
client.start();

SharedCount counter = new SharedCount(client, "/counters/A", 0);
counter.start();

counter.setCount(counter.getCount() + 1);

assertThat(counter.getCount()).isEqualTo(1);
```

在此示例中，Zookeeper 将Integer值存储在路径/counters/A中，如果尚未创建路径，则将值初始化为0 。

## 八. 总结

在本文中，我们了解了如何使用 Apache Curator 连接到 Apache Zookeeper 并利用其主要功能。

我们还介绍了 Curator 中的一些主要食谱。