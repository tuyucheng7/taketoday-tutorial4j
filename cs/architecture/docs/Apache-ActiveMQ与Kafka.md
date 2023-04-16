## 1. 概述

在分布式架构中，应用程序通常需要在它们之间交换数据。一方面，这可以通过彼此直接沟通来完成。另一方面，为了达到高可用性和分区容忍度，以及在应用程序之间获得松耦合，消息传递是一个合适的解决方案。

因此，我们可以在多种产品之间进行选择。Apache 基金会提供了 ActiveMQ 和 Kafka，我们将在本文中对它们进行比较。

## 2. 一般事实

### 2.1. 活跃的MQ

Active MQ是传统的消息代理之一，其目标是保证应用程序之间以安全可靠的方式进行数据交换。它处理少量数据，因此专门用于定义明确的消息格式和事务性消息传递。

我们必须注意到，除了这个“经典”版本之外，还有另一个版本：Active MQ Artemis。这个下一代代理基于 HornetQ，它的代码在 2015 年被 RedHat 提供给 Apache 基金会。在[Active MQ 网站](https://activemq.apache.org/)上，据说：

>   一旦 Artemis 达到与“经典”代码库足够的功能对等水平，它将成为 ActiveMQ 的下一个主要版本。

因此，为了进行比较，我们需要考虑这两个版本。我们将使用术语“Active MQ”和“Artemis”来区分它们。

### 2.2. 卡夫卡

与 Active MQ 不同，Kafka 是一个用于处理大量数据的分布式系统。我们可以将它用于传统消息传递以及：

-   网站活动跟踪
-   指标
-   日志聚合
-   流处理
-   事件溯源
-   提交日志

随着使用微服务构建的典型云架构的出现，这些要求变得非常重要。

### 2.3. JMS 的作用和消息传递的演变

Java 消息服务 (JMS) 是用于在 Java EE 应用程序中发送和接收消息的通用 API。它是消息传递系统早期发展的一部分，至今仍是标准。在 Jakarta EE 中，它被采用为Jakarta Messaging。因此，了解核心概念可能会有所帮助：

-   Java 原生但独立于供应商的 API
-   需要JCA 资源适配器来实现特定于供应商的通信协议
-   消息目标模型：
    -   队列( P2P ) 确保消息排序和一次性消息处理，即使在多个消费者的情况下也是如此
    -   主题( PubSub ) 作为发布-订阅模式的实现，这意味着多个消费者将在订阅主题期间接收消息
-   消息格式：
    -   标头作为经纪人处理的标准化元信息(如优先级或到期日期)
    -   作为消费者可用于消息处理的非标准化元信息的属性
    -   包含有效负载的主体——JMS 声明了五种类型的消息，但这仅与使用 API 相关，与此比较无关

然而，演变朝着开放和独立的方向发展——独立于消费者和生产者的平台，独立于消息代理的供应商。有一些协议定义了它们自己的目的地模型：

-   [AMQP——](https://www.amqp.org/)独立于供应商消息传递的二进制协议——使用通用节点
-   [MQTT——](https://mqtt.org/)嵌入式系统和物联网的轻量级二进制协议——使用主题
-   [STOMP——](https://stomp.github.io/)一种简单的基于文本的协议，甚至允许从浏览器发送消息——使用通用目的地

另一个发展是通过云架构的传播，根据“即发即弃”原则，将以前可靠的单个消息传输(“传统消息传递”)添加到处理大量数据中。可以说，Active MQ和Kafka的比较，就是这两种方式的典型代表的比较。例如，Kafka 的替代方案可能是[NATS](https://nats.io/)。

## 3.比较

在本节中，我们将比较 Active MQ 和 Kafka 之间最有趣的架构和开发特性。

### 3.1. 消息目的地模型、协议和 API

Active MQ 完全实现了Queues和Topic的 JMS 消息目的地模型，并将 AMQP、MQTT 和 STOMP 消息映射到它们。例如，STOMP 消息被映射到Topic中的JMS BytesMessage。此外，它还支持[OpenWire](https://activemq.apache.org/openwire)，允许跨语言访问 Active MQ。

Artemis 独立于标准 API 和协议定义了自己的消息目标模型，并且还需要将它们映射到该模型：

-   消息被发送到一个Address，它被赋予一个唯一的名称、一个Routing Type和零个或多个Queues。

-   路由

    类型

    确定消息如何从地址路由到绑定到该地址的队列。定义了两种类型：

    -   ANYCAST：消息被路由到地址上的单个队列
    -   MULTICAST：消息被路由到地址上的每个队列

Kafka 只定义了Topics，它由多个Partitions(至少 1 个)和Replicas组成，可以放在不同的 broker 上。找到划分主题的最佳策略是一项挑战。我们必须注意：

-   一条消息分布到一个分区中。
-   只有一个分区内的消息才能确保排序。
-   默认情况下，后续消息在主题的分区之间循环分发。
-   如果我们使用消息键，那么具有相同键的消息将落在同一个分区中。

Kafka 有自己的[API](https://kafka.apache.org/documentation/#api)。尽管还有一个[JMS 资源适配器](https://docs.payara.fish/enterprise/docs/documentation/ecosystem/cloud-connectors/apache-kafka.html)，但我们应该意识到这些概念并不完全兼容。官方不支持 AMQP、MQTT 和 STOMP，但有用于[AMQP](https://github.com/ppatierno/kafka-connect-amqp)和[MQTT的](https://github.com/johanvandevenne/kafka-connect-mqtt)[连接器](https://www.baeldung.com/kafka-connectors-guide)。

### 3.2. 消息格式和处理

Active MQ 支持由标头、属性和主体(如上所述)组成的 JMS 标准消息格式。代理必须维护每条消息的传递状态，从而导致吞吐量降低。由于它受 JMS 支持，消费者可以从目的地同步拉取消息，或者消息可以由代理异步推送。

Kafka 没有定义任何消息格式——这完全是生产者的责任。每条消息没有任何传递状态，只有每个消费者和分区的偏移量。偏移量是最后传递的消息的索引。这不仅速度更快，而且还允许通过重置偏移量来重新发送消息，而无需询问生产者。

### 3.3. Spring 和 CDI 集成

JMS 是 Java/Jakarta EE 标准，因此已完全集成到 Java/Jakarta EE 应用程序中。因此，与 Active MQ 和 Artemis 的连接很容易由应用程序服务器管理。借助 Artemis，我们甚至可以使用[嵌入式代理](https://activemq.apache.org/components/artemis/documentation/latest/cdi-integration.html)。[对于 Kafka，托管连接仅在使用Resource Adapter for JMS](https://docs.payara.fish/enterprise/docs/documentation/ecosystem/cloud-connectors/apache-kafka.html)或[Eclipse MicroProfile Reactive](https://dzone.com/articles/using-jakarta-eemicroprofile-to-connect-to-apache)时可用。

Spring 集成了[JMS](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#jms)以及[AMQP](https://spring.io/projects/spring-amqp)、[MQTT](https://docs.spring.io/spring-integration/reference/html/mqtt.html#mqtt)和[STOMP](https://docs.spring.io/spring-integration/reference/html/stomp.html#stomp)。还支持[卡夫卡。](https://spring.io/projects/spring-kafka)[借助 Spring Boot，我们可以为Active MQ](https://memorynotfound.com/spring-boot-embedded-activemq-configuration-example/)、[Artemis](https://activemq.apache.org/components/artemis/documentation/latest/spring-integration.html)和[Kafka](https://www.baeldung.com/spring-boot-kafka-testing)使用嵌入式代理。

## 4. Active MQ/Artemis 和 Kafka 的用例

以下几点为我们指明了何时最好使用哪种产品。

### 4.1. Active MQ/Artemis 的用例

-   每天只处理少量消息
-   高水平的可靠性和事务性
-   即时数据转换，ETL 作业

### 4.2. 卡夫卡的用例

-   处理大量数据
    -   实时数据处理
    -   应用程序活动跟踪
    -   记录和监控
-   没有数据转换的消息传递(这是可能的，但并不容易)
-   没有传输保证的消息传递(这是可能的，但并不容易)

## 5.总结

正如我们所见，Active MQ/Artemis 和 Kafka 都有它们的目的，因此也有它们的理由。重要的是要了解它们的差异，以便为正确的案例选择正确的产品。下表再次简要解释了这些差异：

|             标准             |                         主动 MQ 经典                         |                       主动 MQ Artemis                        |          卡夫卡          |
| :--------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------: |
|             用例             |               传统消息传递(可靠的，事务性的)               |                         分布式事件流                         |                          |
|           P2P消息            |                             尾巴                             |                  路由类型为 ANYCAST 的地址                   |            –             |
|         发布订阅消息         |                             主题                             |                具有路由类型 MULTICAST 的地址                 |           主题           |
|           API/协议           |               JMS，AMQP。MQTT、STOMP、OpenWire               |      Kafka 客户端、AMQP 和 MQTT 连接器、JMS 资源适配器       |                          |
| 基于拉取与基于推送的消息传递 |                           基于推送                           |                            基于拉                            |                          |
|        消息传递的责任        |                   生产者必须确保消息被传递                   |                  消费者消费它应该消费的消息                  |                          |
|           交易支持           |                           JMS, XA                            | [自定义事务管理器](https://www.baeldung.com/kafka-exactly-once) |                          |
|           可扩展性           | [经纪人网络](https://activemq.apache.org/networks-of-brokers.html) | [集群](https://activemq.apache.org/components/artemis/documentation/1.0.0/clusters.html) | 高度可扩展(分区和副本) |
|         消费者越多…          |                          ……性能越慢                          |                           ……不减速                           |                          |

 