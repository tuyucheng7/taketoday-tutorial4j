---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker 是软件行业中用于创建、打包和部署应用程序的最流行的容器引擎之一。

在本教程中，我们将学习如何使用Docker进行[Apache Kafka](https://www.baeldung.com/spring-kafka#overview)设置。

## 延伸阅读：

## [使用Spring介绍Apache Kafka](https://www.baeldung.com/spring-kafka)

将Apache Kafka与Spring结合使用的快速实用指南。

[阅读更多](https://www.baeldung.com/spring-kafka)→

## [使用 MQTT 和 MongoDB 的Kafka连接示例](https://www.baeldung.com/kafka-connect-mqtt-mongodb)

看一下使用Kafka连接器的实际示例。

[阅读更多](https://www.baeldung.com/kafka-connect-mqtt-mongodb)→

## [Kafka Streams 与Kafka消费者](https://www.baeldung.com/java-kafka-streams-vs-kafka-consumer)

了解KafkaStreams 如何简化从Kafka主题检索消息时的处理操作。

[阅读更多](https://www.baeldung.com/java-kafka-streams-vs-kafka-consumer)→

## 2.单节点设置

单节点Kafkabroker 设置可以满足大部分本地开发需求，所以让我们从学习这个简单的设置开始。

### 2.1. docker-compose.yml配置

要启动Apache Kafka服务器，我们首先需要启动[Zookeeper](https://www.baeldung.com/java-zookeeper#overview)服务器。

我们可以在[docker-compose.yml](https://www.baeldung.com/docker-compose)文件中配置这种依赖关系，这将确保Zookeeper服务器始终在Kafka服务器之前启动并在Kafka服务器之后停止。

让我们创建一个简单的docker-compose.yml文件，其中包含两个服务，即zookeeper 和kafka：

```yaml
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 22181:2181
  
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

在此设置中，我们的Zookeeper服务器正在侦听端口 = 2181以获取在同一容器设置中定义的kafka服务。但是，对于在主机上运行的任何客户端，它将暴露在端口22181上。

同样，kafka服务通过端口29092暴露给主机应用程序，但它实际上是在KAFKA_ADVERTISED_LISTENERS属性配置的容器环境中的端口9092上发布的。

### 2.2. 启动卡夫卡服务器

[让我们通过使用docker-compose](https://docs.docker.com/compose/reference/)命令启动容器来启动Kafka服务器：

```shell
$ docker-compose up -d
Creating network "kafka_default" with the default driver
Creating kafka_zookeeper_1 ... done
Creating kafka_kafka_1     ... done
```

现在让我们使用[nc](https://www.baeldung.com/linux/netcat-command#scanning-for-open-ports-using-netcat)命令来验证两个服务器是否都在各自的端口上侦听：

```shell
$ nc -z localhost 22181
Connection to localhost port 22181 [tcp/*] succeeded!
$ nc -z localhost 29092
Connection to localhost port 29092 [tcp/*] succeeded!
```

此外，我们还可以在容器启动时检查详细日志，并验证Kafka服务器是否已启动：

```shell
$ docker-compose logs kafka | grep -i started
kafka_1      | [2021-04-10 22:57:40,413] DEBUG [ReplicaStateMachine controllerId=1] Started replica state machine with initial state -> HashMap() (kafka.controller.ZkReplicaStateMachine)
kafka_1      | [2021-04-10 22:57:40,418] DEBUG [PartitionStateMachine controllerId=1] Started partition state machine with initial state -> HashMap() (kafka.controller.ZkPartitionStateMachine)
kafka_1      | [2021-04-10 22:57:40,447] INFO [SocketServer brokerId=1] Started data-plane acceptor and processor(s) for endpoint : ListenerName(PLAINTEXT) (kafka.network.SocketServer)
kafka_1      | [2021-04-10 22:57:40,448] INFO [SocketServer brokerId=1] Started socket server acceptors and processors (kafka.network.SocketServer)
kafka_1      | [2021-04-10 22:57:40,458] INFO [KafkaServer id=1] started (kafka.server.KafkaServer)
```

这样，我们的Kafka设置就可以使用了。

### 2.3. 使用Kafka工具连接

最后，让我们使用[Kafka Tool](https://kafkatool.com/download.html) GUI 实用程序与我们新创建的Kafka服务器建立连接，稍后，我们将可视化此设置：

[![截图 2021-04-11-at-4.51.32-AM](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-4.51.32-AM.png)](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-4.51.32-AM.png)

我们必须注意，我们需要使用Bootstrap servers属性连接到监听主机端口29092的Kafka服务器。

最后，我们应该能够在左侧栏中可视化连接：

[![截图 2021-04-11-at-5.46.48-AM](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-5.46.48-AM.png)](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-5.46.48-AM.png)

因此，主题和消费者的条目是空的，因为它是一个新设置。创建主题后，我们应该能够跨分区可视化数据。此外，如果有活跃的消费者连接到我们的Kafka服务器，我们也可以查看他们的详细信息。

## 3.Kafka集群设置

对于更稳定的环境，我们需要一个有弹性的设置。让我们扩展我们的docker-compose.yml文件来创建一个多节点Kafka集群设置。

### 3.1. docker-compose.yml配置

Apache Kafka的集群设置需要为Zookeeper服务器和Kafka服务器提供冗余。

因此，让我们为Zookeeper和Kafka服务分别添加一个节点的配置：

```yaml
---
version: '2'
services:
  zookeeper-1:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 22181:2181

  zookeeper-2:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 32181:2181
  
  kafka-1:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper-1
      - zookeeper-2

    ports:
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:2181,zookeeper-2:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-1:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
  kafka-2:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper-1
      - zookeeper-2
    ports:
      - 39092:39092
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:2181,zookeeper-2:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-2:9092,PLAINTEXT_HOST://localhost:39092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

```

我们必须确保服务名称和KAFKA_BROKER_ID在服务中是唯一的。

此外，每个服务都必须向主机公开一个唯一的端口。尽管zookeeper-1和zookeeper-2正在侦听端口2181 ，但它们分别通过端口22181和32181将其暴露给主机。相同的逻辑适用于kafka-1和kafka-2服务，它们将分别监听端口29092和39092。

### 3.2. 启动Kafka集群

让我们使用docker-compose命令启动集群：

```shell
$ docker-compose up -d
Creating network "kafka_default" with the default driver
Creating kafka_zookeeper-1_1 ... done
Creating kafka_zookeeper-2_1 ... done
Creating kafka_kafka-2_1     ... done
Creating kafka_kafka-1_1     ... done
```

集群启动后，让我们使用Kafka工具通过为Kafka服务器和相应端口指定逗号分隔值来连接到集群：

[![截图 2021-04-11-at-5.29.01-AM](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-5.29.01-AM-1024x347.png)](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-5.29.01-AM.png)

最后，让我们看一下集群中可用的多个代理节点：

[![截图 2021-04-11-at-5.30.10-AM](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-5.30.10-AM.png)](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-2021-04-11-at-5.30.10-AM.png)

## 4. 总结

在本文中，我们使用Docker技术创建了Apache Kafka的单节点和多节点设置。

我们还使用了Kafka工具来连接和可视化已配置的代理服务器详细信息。
