## 1. 概述

在本教程中，我们将简要介绍[Apache Kafka](https://kafka.apache.org/)，然后了解如何以编程方式在 Kafka 集群中创建和配置主题。

## 2.卡夫卡简介

Apache Kafka 是一个功能强大的高性能分布式事件流平台。

通常，生产者应用程序向 Kafka 发布事件，而消费者订阅这些事件以读取和处理它们。Kafka 使用 主题来存储和分类这些事件，例如，在电子商务应用程序中，可能有一个“订单”主题。

Kafka 主题是分区的，它跨多个代理分发数据以实现可扩展性。它们可以被以使数据具有容错性和高可用性。即使在消费后，主题也会根据需要保留事件。这一切都是通过 Kafka 命令行工具和键值配置在每个主题的基础上进行管理的。

然而，除了命令行工具，Kafka 还提供了一个 [Admin API](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/Admin.html) 来管理和检查主题、代理和其他 Kafka 对象。在我们的示例中，我们将使用此 API 创建新主题。

## 3.依赖关系

要使用 Admin API，让我们将[kafka-clients 依赖](https://search.maven.org/artifact/org.apache.kafka/kafka-clients)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.8.0</version>
</dependency>
```

## 4.设置卡夫卡

在创建新主题之前，我们至少需要一个单节点的 Kafka 集群。

在本教程中，我们将使用[Testcontainers](https://www.testcontainers.org/) 框架来实例化 Kafka 容器。然后，我们可以运行不依赖于运行的外部 Kafka 服务器的可靠且独立的集成测试。为此，我们需要另外两个专门用于测试的依赖项。

首先，让我们将[Testcontainers Kafka 依赖](https://search.maven.org/artifact/org.testcontainers/kafka)项添加 到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
</dependency>
```

接下来，我们将添加 用于使用 JUnit 5 运行 Testcontainer 测试的[junit-jupiter 工件：](https://search.maven.org/search?q=g:org.testcontainers AND a:junit-jupiter)

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
</dependency>
```

现在我们已经配置了所有必要的依赖项，我们可以编写一个简单的应用程序以编程方式创建新主题。

## 5.管理API

让我们首先为本地代理创建一个具有最少配置的新[Properties实例：](https://www.baeldung.com/java-properties)

```java
Properties properties = new Properties();
properties.put(
  AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers()
);
```

现在我们可以获得一个Admin实例：

```java
Admin admin = Admin.create(properties)
```

create方法接受具有[bootstrap.servers](https://kafka.apache.org/documentation/#adminclientconfigs_bootstrap.servers)属性的Properties对象(或Map) 并返回一个线程安全的实例。

管理客户端使用此属性来发现集群中的代理并随后执行任何管理操作。因此，通常包含两个或三个代理地址就足够了，以涵盖某些实例不可用的可能性。

[AdminClientConfig](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/AdminClientConfig.html)类包含所有[管理客户端配置](https://kafka.apache.org/documentation/#adminclientconfigs)条目的常量。

## 6. 话题创作

[让我们首先使用 Testcontainers](https://www.testcontainers.org/quickstart/junit_5_quickstart/)创建一个JUnit 5 测试 来验证主题创建是否成功。我们将使用[Kafka 模块](https://www.testcontainers.org/modules/kafka/)，它使用[Confluent OSS 平台](https://hub.docker.com/r/confluentinc/cp-kafka/)的官方 Kafka Docker 镜像：

```java
@Test
void givenTopicName_whenCreateNewTopic_thenTopicIsCreated() throws Exception {
    kafkaTopicApplication.createTopic("test-topic");

    String topicCommand = "/usr/bin/kafka-topics --bootstrap-server=localhost:9092 --list";
    String stdout = KAFKA_CONTAINER.execInContainer("/bin/sh", "-c", topicCommand)
      .getStdout();

    assertThat(stdout).contains("test-topic");
}
```

在这里，Testcontainers 将在测试执行期间自动实例化和管理 Kafka 容器。我们只需调用我们的应用程序代码并验证主题是否已在运行的容器中成功创建。

### 6.1. 使用默认选项创建

[主题分区和因子](https://www.baeldung.com/apache-kafka-data-modeling)是新主题的关键考虑因素。我们将保持简单并使用 1 个分区和 1 的因子创建我们的示例主题：

```java
try (Admin admin = Admin.create(properties)) {
    int partitions = 1;
    short replicationFactor = 1;
    NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
    
    CreateTopicsResult result = admin.createTopics(
      Collections.singleton(newTopic)
    );

    KafkaFuture<Void> future = result.values().get(topicName);
    future.get();
}
```

在这里，我们使用了Admin。createTopics方法使用默认选项创建一批新主题。由于 Admin接口扩展了AutoCloseable接口，我们使用 [try-with-resources](https://www.baeldung.com/java-try-with-resources)来执行我们的操作。这确保资源被适当地释放。

重要的是，此方法与 Controller Broker 通信并异步执行。返回的 CreateTopicsResult对象公开了一个KafkaFuture，用于访问请求批次中每个项目的结果。这遵循Java异步编程模式，并允许调用者使用 [Future.get](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#get())方法获取操作结果。

对于同步行为，我们可以立即调用此方法来检索我们的操作结果。这会阻塞，直到操作完成或失败。如果失败，它会导致 ExecutionException 包装根本原因。

### 6.2. 使用选项创建

除了默认选项，我们还可以使用Admin 的重载形式。createTopics 方法并通过[CreateTopicsOptions](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/CreateTopicsOptions.html)对象提供一些选项。在创建新主题时，我们可以使用这些来修改管理客户端的行为：

```java
CreateTopicsOptions topicOptions = new CreateTopicsOptions()
  .validateOnly(true)
  .retryOnQuotaViolation(false);

CreateTopicsResult result = admin.createTopics(
  Collections.singleton(newTopic), topicOptions
);
```

在这里，我们将validateOnly选项设置为 true，这意味着客户端只会在不实际创建主题的情况下进行验证。同样，将retryOnQuotaViolation选项设置为 false，以便在违反配额的情况下不会重试该操作。

### 6.3. 新主题配置

Kafka 具有广泛的[主题配置](https://kafka.apache.org/documentation.html#topicconfigs)来控制主题行为，例如[数据保留](https://www.baeldung.com/kafka-message-retention)和压缩等。这些配置既有服务器默认值，也有可选的每个主题覆盖。

我们可以 通过为新主题使用配置映射来提供主题配置：

```java
// Create a compacted topic with 'lz4' compression codec
Map<String, String> newTopicConfig = new HashMap<>();
newTopicConfig.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
newTopicConfig.put(TopicConfig.COMPRESSION_TYPE_CONFIG, "lz4");

NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor)
  .configs(newTopicConfig);
```

Admin API 中的[TopicConfig](https://kafka.apache.org/28/javadoc/org/apache/kafka/common/config/TopicConfig.html)类包含可用于在创建时配置主题的键。

## 七、其他话题操作

除了创建新主题的能力外，Admin API 还具有[删除](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/Admin.html#deleteTopics(java.util.Collection))、[列出](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/Admin.html#listTopics())和[描述](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/Admin.html#describeTopics(java.util.Collection))主题的操作。所有这些与主题相关的操作都遵循与我们在创建主题时看到的相同的模式。

这些操作方法中的每一个都有一个以xxxTopicOptions对象作为输入的重载版本。所有这些方法都返回相应的 xxxTopicsResult 对象。反过来，这提供了 用于访问异步操作结果的KafkaFuture 。

最后，还值得一提的是，自从在 Kafka 版本 0.11.0.0 中引入以来，管理 API 仍在不断发展，如 InterfaceStability.Evolving注解所示。这意味着 API 将来可能会发生变化，次要版本可能会破坏兼容性。

## 八. 总结

在本教程中，我们了解了如何使用Java管理客户端在 Kafka 中创建新主题。

最初，我们创建了一个默认的主题，然后使用显式选项。在此之后，我们看到了如何使用各种属性配置新主题。最后，我们简要介绍了使用管理客户端的其他与主题相关的操作。

在此过程中，我们还看到了如何使用 Testcontainers 从我们的测试中设置一个简单的单节点集群。