## 1. 简介

[Apache Pulsar](https://pulsar.apache.org/)是 Yahoo 开发的基于分布式开源发布/订阅的消息传递系统。

它的创建是为了支持 Yahoo Mail、Yahoo Finance、Yahoo Sports 等雅虎的关键应用程序。然后，在 2016 年，它在 Apache 软件基金会下开源。

## 2. 架构

Pulsar 是用于服务器到服务器消息传递的多租户、高性能解决方案。它由一组代理和 bookies 以及用于配置和管理的内置[Apache ZooKeeper组成。](https://zookeeper.apache.org/)这些 bookies 来自[Apache BookKeeper](https://bookkeeper.apache.org/) ，它为消息提供存储，直到它们被使用。

在集群中，我们将拥有：

-   多个集群代理处理来自生产者的传入消息并将消息分发给消费者
-   Apache BookKeeper 支持消息持久化
-   Apache ZooKeeper 存储集群配置

为了更好地理解这一点，让我们看一下[文档](https://pulsar.apache.org/docs/en/concepts-architecture-overview/)中的架构图：

[![脉冲星系统架构](https://www.baeldung.com/wp-content/uploads/2018/10/pulsar-system-architecture-1024x677.png)](https://www.baeldung.com/wp-content/uploads/2018/10/pulsar-system-architecture.png)

## 3. 主要特点

让我们从快速浏览一些主要功能开始：

-   对多个集群的内置支持 
-   支持跨多个集群的消息地理
-   多种订阅模式
-   可扩展到数百万个主题
-   使用 Apache BookKeeper 来保证消息传递。
-   低延迟

现在，让我们详细讨论一些关键特性。

### 3.1. 消息模型

该框架提供了一个灵活的消息传递模型。一般来说，消息传递架构有两种消息传递模型，即排队和发布者/订阅者。发布者/订阅者是一个广播消息系统，消息被发送给所有的消费者。另一方面，排队是点对点的通信。

Pulsar 在一个通用 API 中结合了这两个概念。发布者将消息发布到不同的主题。然后将这些消息广播到所有订阅。

消费者订阅以获取消息。该库允许消费者在同一订阅中选择不同的方式来消费消息，包括独占、共享和故障转移。我们将在后面的部分详细讨论这些订阅类型。

### 3.2. 部署模式

Pulsar 内置了对在不同环境中部署的支持。这意味着我们可以在标准的本地机器上使用它，或者将它部署在 Kubernetes 集群、谷歌或 AWS 云中。

出于开发和测试目的，它可以作为单个节点执行。在这种情况下，所有组件(代理、BookKeeper 和 ZooKeeper)都在单个进程中运行。

### 3.3. 地理

该库为数据的异地提供开箱即用的支持。 我们可以通过配置不同的地理区域来实现多个集群之间的消息。

消息数据几乎是实时的。如果跨集群发生网络故障，数据始终是安全的并存储在 BookKeeper 中。系统不断重试，直到成功。

异地功能还允许组织跨不同的云提供商部署 Pulsar 并数据。这有助于他们避免使用专有的云提供商 API。

### 3.4. 持久性

Pulsar 读取并确认数据后，保证不丢失数据。数据持久性与配置用于存储数据的磁盘数量有关。

Pulsar 通过使用在存储节点中运行的 bookies(Apache BookKeeper 实例)来确保持久性。每当 bookie 收到消息时，它都会在内存中保存一份副本，并将数据写入 WAL(预写日志)。此日志的工作方式与数据库 WAL 相同。Bookies基于数据库事务原理运行，即使在机器故障的情况下也能保证数据不丢失。

除此之外，Pulsar 还可以承受多节点故障。库将数据到多个 bookie，然后向生产者发送确认消息。这种机制保证即使在多个硬件故障的情况下也能实现零数据丢失。

## 4.单节点设置

现在让我们看看如何搭建 Apache Pulsar 的单节点集群。

Apache 还提供了一个简单的 [客户端 API](https://pulsar.apache.org/docs/en/concepts-clients/) ，其中包含 Java、Python 和 C++ 的绑定。稍后我们将创建一个简单的Java生产者和订阅示例。

### 4.1. 安装

Apache Pulsar 可作为二进制发行版使用。让我们从下载它开始：

```shell
wget https://archive.apache.org/dist/incubator/pulsar/pulsar-2.1.1-incubating/apache-pulsar-2.1.1-incubating-bin.tar.gz
```

下载完成后，我们可以解压缩 zip 文件。未归档的发行版将包含bin、conf、example、licenses 和lib文件夹。

之后，我们需要下载内置连接器。这些现在作为单独的包裹运送：

```shell
wget https://archive.apache.org/dist/incubator/pulsar/pulsar-2.1.1-incubating/apache-pulsar-io-connectors-2.1.1-incubating-bin.tar.gz
```

让我们取消存档连接器并 Pulsar 文件夹中的Connectors 文件夹。

### 4.2. 启动实例

要启动独立实例，我们可以执行：

```shell
bin/pulsar standalone
```

## 5.Java客户端

现在我们将创建一个Java项目来生成和使用消息。我们还将为不同的订阅类型创建示例。

### 5.1. 设置项目

我们将从将[pulsar-client](https://search.maven.org/search?q=a:pulsar-client AND g:org.apache.pulsar)依赖项添加到我们的项目开始：

```xml
<dependency>
    <groupId>org.apache.pulsar</groupId>
    <artifactId>pulsar-client</artifactId>
    <version>2.1.1-incubating</version>
</dependency>
```

### 5.2. 制作人

让我们继续创建一个生产者示例。在这里，我们将创建一个主题和一个生产者。

首先，我们需要创建一个 PulsarClient ，它将使用自己的协议连接到特定主机和端口上的 Pulsar 服务 。许多生产者和消费者可以共享一个客户端对象。

现在，我们将创建一个 具有特定主题名称的生产者：

```java
private static final String SERVICE_URL = "pulsar://localhost:6650";
private static final String TOPIC_NAME = "test-topic";

PulsarClient client = PulsarClient.builder()
  .serviceUrl(SERVICE_URL)
  .build();

Producer<byte[]> producer = client.newProducer()
  .topic(TOPIC_NAME)
  .compressionType(CompressionType.LZ4)
  .create();
```

生产者将发送 5 条消息：

```java
IntStream.range(1, 5).forEach(i -> {
    String content = String.format("hi-pulsar-%d", i);

    Message<byte[]> msg = MessageBuilder.create()
      .setContent(content.getBytes())
      .build();
    MessageId msgId = producer.send(msg);
});
```

### 5.3. 消费者

接下来，我们将创建消费者以获取生产者创建的消息。消费者还需要相同的PulsarClient 来连接我们的服务器：

```java
Consumer<byte[]> consumer = client.newConsumer()
  .topic(TOPIC_NAME)
  .subscriptionType(SubscriptionType.Shared)
  .subscriptionName(SUBSCRIPTION_NAME)
  .subscribe();

```

在这里，我们创建了一个 共享订阅类型的客户端。 这允许多个消费者附加到同一个订阅并获取消息。

### 5.4. 消费者订阅类型

在上面的消费者示例中，我们创建了一个共享类型的订阅。我们还可以创建 独占和故障转移订阅。

独占 订阅只允许订阅一个消费者。

另一方面，af ailover 订阅 允许用户定义回退消费者，以防一个消费者失败，如 Apache 图中所示：

[![脉冲星订阅模式](https://www.baeldung.com/wp-content/uploads/2018/10/pulsar-subscription-modes-1024x753.png)](https://www.baeldung.com/wp-content/uploads/2018/10/pulsar-subscription-modes.png)

## 六. 总结

在本文中，我们重点介绍了 Pulsar 消息传递系统的特性，例如消息传递模型、地理和强大的持久性保证。

我们还学习了如何设置单个节点以及如何使用Java客户端。