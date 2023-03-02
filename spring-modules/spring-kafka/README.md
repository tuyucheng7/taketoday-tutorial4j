## Spring Kafka

本模块包含有关Spring集成Kafka的文章

## 相关文章

+ [Spring与Apache Kafka的使用介绍](docs/Spring与Apache-Kafka的使用介绍.md)
+ [Kafka Streams使用Spring Boot](docs/Kafka-Streams使用SpringBoot.md)
+ [使用Kafka发送大消息](docs/使用Kafka发送大消息.md)
+ [使用Spring Boot配置Kafka SSL](docs/使用SpringBoot配置Kafka-SSL.md)
+ [测试Kafka和Spring Boot](docs/测试Kafka和SpringBoot.md)
+ [监控Apache Kafka中的消费者延迟](docs/监控Apache-Kafka中的消费者延迟.md)
+ [获取Apache Kafka主题中的消息数](docs/获取Apache-Kafka主题中的消息数.md)

- 更多文章： [[next -->]](../spring-kafka-2/README.md)

## 介绍

这是一个简单的Spring Boot应用程序，用于演示使用spring-kafka在Kafka中发送和接收消息。

由于默认情况下不会自动创建Kafka主题，因此此应用程序要求你手动地创建以下主题。

`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic baeldung`<br>
`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic partitioned`<br>
`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic filtered`<br>
`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic greeting`<br>

应用程序成功运行时，以下输出将记录到控制台(以及spring日志)：

### 基本监听器从“tuyucheng”主题收到的消息，带有组foo和bar

> Received Message in group 'foo': Hello, World!<br>
> Received Message in group 'bar': Hello, World!

### 从“tuyucheng”主题收到的消息，其中包含分区信息

> Received Message: Hello, World! from partition: 0

### 从“partitioned”主题接收的消息，仅来自特定分区

> Received Message: Hello To Partioned Topic! from partition: 0<br>
> Received Message: Hello To Partioned Topic! from partition: 3

### 过滤后从“filtered”主题收到的消息

> Received Message in filtered listener: Hello Tuyucheng!

### 从“greeting”主题接收的消息(序列化的Java对象)

> Received greeting message: Greetings, World!!