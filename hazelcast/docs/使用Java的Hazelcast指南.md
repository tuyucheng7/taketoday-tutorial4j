## 1. 概述

这是一篇关于 Hazelcast 的介绍性文章，我们将在其中了解如何创建集群成员、分布式Map以在集群节点之间共享数据，以及创建Java客户端以连接和查询集群中的数据。

## 2. 什么是 Hazelcast？

Hazelcast 是用于Java的分布式内存数据网格平台。该体系结构支持集群环境中的高可扩展性和数据分布。支持节点自动发现和智能同步。

Hazelcast 有[不同的版本](http://docs.hazelcast.org/docs/latest/manual/html-single/index.html#hazelcast-editions.)。要查看所有 Hazelcast 版本的功能，我们可以参考以下[链接](https://hazelcast.org/imdg/imdg-features/)。在本教程中，我们将使用开源版本。

同样，Hazelcast 提供了各种功能，例如分布式数据结构、分布式计算、分布式查询等。出于本文的目的，我们将重点关注分布式Map。

## 3.Maven依赖

Hazelcast 提供了许多不同的库来处理各种需求。我们可以在 Maven Central的[com.hazelcast组下找到它们。](https://search.maven.org/classic/#search|ga|1|g%3A"com.hazelcast")

但是，在本文中，我们将仅使用创建独立的 Hazelcast 集群成员和 HazelcastJava客户端所需的核心依赖项：

```xml
<dependency>
    <groupId>com.hazelcast</groupId>
    <artifactId>hazelcast</artifactId>
    <version>4.0.2</version>
</dependency>

```

当前版本在[maven central repository](https://search.maven.org/classic/#search|gav|1|g%3A"com.hazelcast" AND a%3A"hazelcast")中可用。

## 4. 第一个 Hazelcast 应用程序

### 4.1. 创建 Hazelcast 成员

成员(也称为节点)自动连接在一起形成集群。这种自动加入是通过成员用来找到彼此的各种发现机制进行的。

让我们创建一个将数据存储在 Hazelcast 分布式地图中的成员：

```java
public class ServerNode {
    
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
    ...
}
```

当我们启动ServerNode应用程序时，我们可以在控制台中看到流动的文本，这意味着我们在 JVM 中创建了一个新的 Hazelcast 节点，它必须加入集群。

```xml
Members [1] {
    Member [192.168.1.105]:5701 - 899898be-b8aa-49aa-8d28-40917ccba56c this
}

```

要创建多个节点，我们可以启动ServerNode应用程序的多个实例。因此，Hazelcast 将自动创建一个新成员并将其添加到集群中。

例如，如果我们再次运行ServerNode应用程序，我们将在控制台中看到以下日志，表明集群中有两个成员。

```xml
Members [2] {
  Member [192.168.1.105]:5701 - 899898be-b8aa-49aa-8d28-40917ccba56c
  Member [192.168.1.105]:5702 - d6b81800-2c78-4055-8a5f-7f5b65d49f30 this
}
```

### 4.2. 创建分布式地图

接下来，让我们创建一个分布式地图。我们需要之前创建的HazelcastInstance实例来构造一个扩展java.util.concurrent.ConcurrentMap接口的分布式Map 。

```java
Map<Long, String> map = hazelcastInstance.getMap("data");
...
```

最后，让我们向地图添加一些条目：

```java
FlakeIdGenerator idGenerator = hazelcastInstance.getFlakeIdGenerator("newid");
for (int i = 0; i < 10; i++) {
    map.put(idGenerator.newId(), "message" + i);
}
```

正如我们在上面看到的，我们已经向map添加了 10 个条目。我们使用FlakeIdGenerator来确保我们获得地图的唯一键。有关FlakeIdGenerator的更多详细信息，我们可以查看以下[链接](https://javadoc.io/doc/com.hazelcast/hazelcast/4.0.2/com/hazelcast/flakeidgen/FlakeIdGenerator.html)。

虽然这可能不是真实示例，但我们仅使用它来演示可应用于分布式地图的众多操作之一。稍后，我们将看到如何从 HazelcastJava客户端检索集群成员添加的条目。

在内部，Hazelcast 对映射条目进行分区，并在集群成员之间分发和条目。有关 Hazelcast地图的更多详细信息，我们可以查看以下[链接](https://docs.hazelcast.org/docs/4.0.2/manual/html-single/index.html#map)。

### 4.3. 创建 HazelcastJava客户端

Hazelcast 客户端允许我们在不成为集群成员的情况下执行所有 Hazelcast 操作。它连接到集群成员之一，并将所有集群范围的操作委托给它。

让我们创建一个本地客户端：

```java
ClientConfig config = new ClientConfig();
config.setClusterName("dev");
HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(config);

```

就这么简单。

### 4.4. 从Java客户端访问分布式地图

接下来，我们将使用之前创建的HazelcastInstance实例来访问分布式Map：

```java
Map<Long, String> map = hazelcastInstanceClient.getMap("data");
...
```

现在我们可以在不成为集群成员的情况下对地图进行操作。例如，让我们尝试遍历条目：

```java
for (Entry<Long, String> entry : map.entrySet()) {
    ...
}
```

## 5. 配置 Hazelcast

在本节中，我们将重点介绍如何使用声明式 (XML) 和编程式 (API) 配置 Hazelcast 网络，并使用 Hazelcast 管理中心来监视和管理正在运行的节点。

当 Hazelcast 启动时，它会查找hazelcast.config系统属性。如果已设置，则其值用作路径。否则，Hazelcast 会在工作目录或类路径中搜索hazelcast.xml文件。

如果以上都不起作用，Hazelcast 加载默认配置，即hazelcast.jar附带的hazelcast-default.xml 。

### 5.1. 网络配置

默认情况下，Hazelcast 使用多播来发现可以形成集群的其他成员。如果多播不是我们环境的首选发现方式，那么我们可以为完整的 TCP/IP 集群配置 Hazelcast。

让我们使用声明式配置来配置 TCP/IP 集群：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<hazelcast xmlns="http://www.hazelcast.com/schema/config"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.hazelcast.com/schema/config
                               http://www.hazelcast.com/schema/config/hazelcast-config-4.0.xsd";
    <network>
        <port auto-increment="true" port-count="20">5701</port>
        <join>
            <multicast enabled="false"/>
            <tcp-ip enabled="true">
                <member>machine1</member>
                <member>localhost</member>
            </tcp-ip>
        </join>
    </network>
</hazelcast>
```

或者，我们可以使用Java配置方法：

```xml
Config config = new Config();
NetworkConfig network = config.getNetworkConfig();
network.setPort(5701).setPortCount(20);
network.setPortAutoIncrement(true);
JoinConfig join = network.getJoin();
join.getMulticastConfig().setEnabled(false);
join.getTcpIpConfig()
  .addMember("machine1")
  .addMember("localhost").setEnabled(true);
```

默认情况下，Hazelcast 将尝试绑定 100 个端口。在上面的示例中，如果我们将端口的值设置为 5701 并将端口数限制为 20，当成员加入集群时，Hazelcast 会尝试查找 5701 和 5721 之间的端口。

如果我们想选择只使用一个端口，我们可以通过将auto-increment设置为false来禁用自动增量功能。

### 5.2. 管理中心配置

管理中心允许我们监控集群的整体状态，我们还可以详细分析和浏览数据结构，更新地图配置，并从节点获取线程转储。

要使用 Hazelcast 管理中心，我们可以部署mancenter-version。war应用程序到我们的Java应用程序服务器/容器中，或者我们可以从命令行启动 Hazelcast 管理中心。我们可以从[hazelcast.org](https://hazelcast.org/imdg/download/)下载最新的 Hazelcast ZIP 。ZIP 包含mancenter-version。战争档案。

我们可以通过将 Web 应用程序的 URL 添加到hazelcast.xml 来配置我们的 Hazelcast 节点，然后让 Hazelcast 成员与管理中心通信。

那么现在让我们使用声明式配置来配置管理中心：

```xml
<management-center enabled="true">
    http://localhost:8080/mancenter
</management-center>
```

同样，这是编程配置：

```java
ManagementCenterConfig manCenterCfg = new ManagementCenterConfig();
manCenterCfg.setEnabled(true).setUrl("http://localhost:8080/mancenter");
```

## 六. 总结

在本文中，我们介绍了有关 Hazelcast 的介绍性概念。更多的细节，我们可以看看[参考手册](http://docs.hazelcast.org/docs/3.7/manual/html-single/index.html)。