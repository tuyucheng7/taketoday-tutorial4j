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

## Intro

This is a simple Spring Boot app to demonstrate sending and receiving of messages in Kafka using spring-kafka.

As Kafka topics are not created automatically by default, this application requires that you create the following topics
manually.

`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic baeldung`<br>
`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic partitioned`<br>
`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic filtered`<br>
`$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic greeting`<br>

When the application runs successfully, following output is logged on to console (along with spring logs):

### Message received from the 'baeldung' topic by the basic listeners with groups foo and bar

> Received Message in group 'foo': Hello, World!<br>
> Received Message in group 'bar': Hello, World!

### Message received from the 'baeldung' topic, with the partition info

> Received Message: Hello, World! from partition: 0

### Message received from the 'partitioned' topic, only from specific partitions

> Received Message: Hello To Partioned Topic! from partition: 0<br>
> Received Message: Hello To Partioned Topic! from partition: 3

### Message received from the 'filtered' topic after filtering

> Received Message in filtered listener: Hello Baeldung!

### Message (Serialized Java Object) received from the 'greeting' topic

> Received greeting message: Greetings, World!!