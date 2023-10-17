---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Apache Kafka是一个非常流行的事件流平台，经常与](https://kafka.apache.org/)[Docker](https://www.docker.com/)一起使用。通常，人们会遇到与Kafka建立连接的问题，尤其是当客户端不在同一Docker网络或同一主机上运行时。这主要是由于Kafka的广告侦听器配置错误。

在本教程中，我们将学习如何配置侦听器，以便客户端可以连接到在Docker中运行的Kafka代理。

## 2.设置卡夫卡

在尝试建立连接之前，我们需要[使用Docker运行Kafka代理](https://www.baeldung.com/ops/kafka-docker-setup)。[这是我们的docker-compose.yaml](https://www.baeldung.com/ops/docker-compose)文件的片段：

```json
version: '2'
services:
  zookeeper:
    container_name: zookeeper
    networks: 
      - kafka_network
    ...
  
  kafka:
    container_name: kafka
    networks: 
      - kafka_network
    ports:
      - 29092:29092
    environment:
      KAFKA_LISTENERS: EXTERNAL_SAME_HOST://:29092,INTERNAL://:9092
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka:9092,EXTERNAL_SAME_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL_SAME_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
     ... 

networks:
  kafka_network:
    name: kafka_docker_example_net
```

在这里，我们定义了两个必备服务——Kafka 和Zookeeper。我们还定义了一个自定义网络——kafka_docker_example_net，我们的服务将使用它。

稍后我们将更详细地查看KAFKA_LISTENERS、KAFKA_ADVERTISED_LISTENERS和KAFKA_LISTENER_SECURITY_PROTOCOL_MAP属性。

使用上面的docker-compose.yaml文件，我们启动服务：

```json
docker-compose up -d
Creating network "kafka_docker_example_net" with the default driver
Creating zookeeper ... done
Creating kafka ... done
```

此外，我们将使用 Kafka[控制台生产者](https://kafka-tutorials.confluent.io/kafka-console-consumer-producer-basics/kafka.html)实用程序作为示例客户端来测试与Kafka代理的连接。要在没有Docker的情况下使用 Kafka-console-producer 脚本，我们需要下载[Kafka](https://kafka.apache.org/downloads)。

## 3.听众

在与Kafka代理连接时，侦听器、广告侦听器和侦听器协议起着相当大的作用。

我们使用KAFKA_LISTENERS属性管理侦听器，我们在其中声明一个以逗号分隔的 URI 列表，它指定代理应该侦听传入 TCP 连接的套接字。

每个 URI 包含一个协议名称，后跟一个接口地址和一个端口：

```json
EXTERNAL_SAME_HOST://0.0.0.0:29092,INTERNAL://0.0.0.0:9092
```

在这里，我们指定了一个0.0.0.0元地址来将套接字绑定到所有接口。此外，EXTERNAL_SAME_HOST和INTERNAL是我们在以 URI 格式定义监听器时需要指定的自定义监听器名称。

### 3.2. 自举

对于初始连接，Kafka 客户端需要一个引导服务器列表，我们在其中指定代理的地址。该列表应至少包含一个指向集群中随机代理的有效地址。

客户端将使用该地址连接到代理。如果连接成功，代理将返回有关集群的元数据，包括集群中所有代理的广告侦听器列表。对于后续连接，客户端将使用该列表联系代理。

### 3.3. 广告听众

仅仅声明监听器是不够的，因为它只是代理的套接字配置。我们需要一种方法来告诉客户(消费者和生产者)如何连接到 Kafka。

这是在KAFKA_ADVERTISED_LISTENERS属性的帮助下，广告侦听器出现的地方。它具有与侦听器属性类似的格式：

<listener protocol>://<advertised host name>:<advertised port>

在初始引导过程之后，客户端使用指定为通告侦听器的地址。

### 3.4. 侦听器安全协议映射

除了侦听器和广告侦听器之外，我们还需要告诉客户端连接到Kafka时要使用的安全协议。在KAFKA_LISTENER_SECURITY_PROTOCOL_MAP 中，我们将自定义协议名称映射到有效的安全协议。

在上一节的配置中，我们声明了两个自定义协议名称——INTERNAL和EXTERNAL_SAME_HOST 。我们可以随意命名它们，但我们需要将它们映射到有效的安全协议。

我们指定的安全协议之一是PLAINTEXT，这意味着客户端不需要向Kafka代理进行身份验证。此外，交换的数据未加密。

## 4.客户端从同一个Docker网络连接

让我们从另一个容器启动Kafka控制台生产者并尝试向代理生产消息：

```json
docker run -it --rm --network kafka_docker_example_net confluentinc/cp-kafka /bin/kafka-console-producer --bootstrap-server kafka:9092 --topic test_topic
>hello
>world
```

在这里，我们将这个容器附加到现有的kafka_docker_example_net网络，以便与我们的代理自由通信。我们还指定了代理的地址 – kafka:9092 和将自动创建的主题名称。

我们能够为主题生成消息，这意味着与代理的连接成功。

## 5.客户端从同一主机连接

当客户端未容器化时，让我们从主机连接到代理。对于外部连接，我们公布了EXTERNAL_SAME_HOST侦听器，我们可以使用它来建立与主机的连接。从广告的侦听器属性中，我们知道我们必须使用localhost:29092 地址才能到达Kafka代理。

为了测试同一主机的连接性，我们将使用非Dockerized Kafka控制台生产者：

```json
kafka-console-producer --bootstrap-server localhost:29092 --topic test_topic_2
>hi
>there

```

由于我们设法生成了主题，这意味着初始引导和后续连接(客户端使用广告侦听器)到代理都是成功的。

我们之前在docker-compose.yaml中配置的端口号 29092使Kafka代理可以在Docker外部访问。

## 6.客户端从不同的主机连接

如果Kafka代理运行在不同的主机上，我们将如何连接到它？不幸的是，我们不能重复使用现有的侦听器，因为它们仅适用于相同的Docker网络或主机连接。因此，相反，我们需要定义一个新的侦听器并公布它：

```json
KAFKA_LISTENERS: EXTERNAL_SAME_HOST://:29092,EXTERNAL_DIFFERENT_HOST://:29093,INTERNAL://:9092
KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka:9092,EXTERNAL_SAME_HOST://localhost:29092,EXTERNAL_DIFFERENT_HOST://157.245.80.232:29093
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL_SAME_HOST:PLAINTEXT,EXTERNAL_DIFFERENT_HOST:PLAINTEXT

```

我们创建了一个名为EXTERNAL_DIFFERENT_HOST的新侦听器，其中包含安全协议PLAINTEXT和关联的端口 29093 。在KAFKA_ADVERTISED_LISTENERS中，我们还添加了运行Kafka的云机器的 IP 地址。

我们必须记住，我们不能使用本地主机，因为我们是从不同的机器(在本例中为本地工作站)连接的。此外，端口 29093 在端口部分下发布，因此可以在Docker外部访问。

让我们尝试生成一些消息：

```json
kafka-console-producer --bootstrap-server 157.245.80.232:29093 --topic test_topic_3
>hello
>REMOTE SERVER
```

我们可以看到我们能够连接到Kafka代理并成功生成消息。

## 七、总结

在本文中，我们学习了如何配置侦听器，以便客户端可以连接到在Docker中运行的 Kafka 代理。我们查看了客户端在同一Docker网络、同一主机、不同主机等上运行的不同场景。我们看到侦听器、广告侦听器和安全协议映射的配置决定了连接性。
