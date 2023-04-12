## 1. 概述

[Apache Kafka](https://kafka.apache.org/)是一个功能强大的开源分布式容错事件流平台。但是，当我们使用Kafka发送大于配置大小限制的消息时，它会报错。

我们在之前的教程中展示了[如何使用Spring和Kafka](https://www.baeldung.com/spring-kafka)。在本教程中，我们将了解使用Kafka发送大消息的方法。

## 2. 问题陈述

**Kafka配置限制了允许发送的消息的大小**。默认情况下，此限制为1MB。但是，如果需要发送大消息，我们需要根据我们的要求调整这些配置。

**对于本教程，我们将使用Kafka v2.5**。在开始配置之前，让我们先看看我们的Kafka设置。

## 3. 设置

在这里，我们将使用具有单个代理的基本Kafka设置。此外，生产者应用程序可以使用Kafka Client通过定义的主题向Kafka Broker发送消息。此外，我们使用单个分区主题：

<img src="../assets/img.png">

我们可以在这里观察多个交互点，如Kafka Producer、Kafka Broker、Topic和Kafka Consumer。因此，**所有这些都需要配置更新才能将大消息从一端发送到另一端**。

让我们详细研究这些配置以发送20MB的大消息。

## 3. Kafka生产者配置

这是我们消息的第一个发源地。我们使用Spring Kafka将消息从我们的应用程序发送到Kafka服务器。

因此，**需要首先更新属性“max.request.size”**。有关此生产者配置的更多详细信息，请参阅[Kafka文档](https://kafka.apache.org/documentation/#producerconfigs_max.request.size)。这在Kafka客户端库中作为常量ProducerConfig.MAX_REQUEST_SIZE_CONFIG提供，它作为Spring Kafka依赖项的一部分提供。

让我们将此值配置为20971520字节：

```java
public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "20971520");

    return new DefaultKafkaProducerFactory<>(configProps);
}
```

## 4. Kafka主题配置

我们的消息生产者应用程序根据定义的主题向Kafka Broker发送消息。因此，下一个要求是配置使用的Kafka主题。这意味着我们需要**更新默认值为1MB的“max.message.bytes”属性**。

这保存了压缩后Kafka的最大记录批处理大小的值(如果启用了压缩)。其他详细信息可在[Kafka文档](https://kafka.apache.org/25/documentation.html#max.message.bytes)中找到。

让我们在创建主题时使用CLI命令手动配置此属性：

```shell
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic longMessage --partitions 1 \
--replication-factor 1 --config max.message.bytes=20971520 
```

或者，我们可以通过Kafka客户端配置此属性：

```java
public NewTopic topic() {
    NewTopic newTopic = new NewTopic(longMsgTopicName, 1, (short) 1);
    Map<String, String> configs = new HashMap<>();
    configs.put("max.message.bytes", "20971520");
    newTopic.configs(configs);
    return newTopic;
}
```

至少，我们需要配置这两个属性。

## 5. Kafka代理配置

可选的配置属性“message.max.bytes”可用于允许Broker上的所有主题接收大小超过1MB的消息。

这保存了压缩后Kafka允许的最大记录批大小的值(如果启用了压缩)。其他详细信息可在[Kafka文档](https://kafka.apache.org/25/documentation.html#message.max.bytes)中找到。

让我们在Kafka Broker的“server.properties”配置文件中添加这个属性：

```properties
message.max.bytes=20971520
```

此外，“message.max.bytes”和“max.message.bytes”中的最大值将作为有效值使用。

## 6. 消费者配置

让我们看看可用于Kafka消费者的配置设置。尽管这些更改对于消费大型消息不是强制性的，但避免它们会对消费者应用程序的性能产生影响。因此，最好也设置这些配置：

-   max.partition.fetch.bytes：此属性限制消费者可以从主题的分区中获取的字节数。其他详细信息可在[Kafka文档](https://kafka.apache.org/documentation/#consumerconfigs_max.partition.fetch.bytes)中找到。这在Kafka客户端库中作为名为ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG的常量提供 
-   fetch.max.bytes：此属性限制了消费者可以从Kafka服务器本身获取的字节数。一个Kafka消费者也可以监听多个分区。其他详细信息可在[Kafka文档](https://kafka.apache.org/documentation/#consumerconfigs_fetch.max.bytes)中找到。这在Kafka客户端库中作为常量ConsumerConfig.FETCH_MAX_BYTES_CONFIG提供

因此，要配置我们的消费者，我们需要创建一个KafkaConsumerFactory。请记住，与主题/代理配置相比，我们始终需要使用更高的值：

```java
public ConsumerFactory<String, String> consumerFactory(String groupId) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "20971520");
    props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "20971520");
    return new DefaultKafkaConsumerFactory<>(props);
}
```

在这里，我们为两个属性使用了相同的配置值20971520字节，因为我们使用的是单个分区Topic。但是，FETCH_MAX_BYTES_CONFIG的值应该高于MAX_PARTITION_FETCH_BYTES_CONFIG。当我们让消费者监听多个分区时，FETCH_MAX_BYTES_CONFIG表示可以从多个分区中获取的消息大小。另一方面，配置MAX_PARTITION_FETCH_BYTES_CONFIG表示从单个分区获取消息的大小。

## 7. 备选方案

我们看到了如何更新Kafka生产者、主题、代理和Kafka消费者中的不同配置以发送大消息。但是，我们通常应该避免使用Kafka发送大消息。大消息的处理会消耗我们的生产者和消费者更多的CPU和内存。因此最终在某种程度上限制了它们对其他任务的处理能力。此外，这可能会给最终用户带来明显的高延迟。

让我们看看其他可能的选择：

1.  Kafka生产者提供了压缩消息的功能。此外，它支持我们可以使用compression.type属性配置的不同压缩类型。
2.  我们可以将大消息存储在共享存储位置的文件中，并通过Kafka消息发送该位置。这可能是一个更快的选项并且具有最小的处理开销。
3.  另一种选择是在生产者端将大消息拆分为每条大小为1KB的小消息。之后，我们可以使用分区键将所有这些消息发送到单个分区，以确保正确的顺序。因此，稍后，在消费者端，我们可以从较小的消息中重构出较大的消息。

如果以上选项都不符合我们的要求，我们可以选择前面讨论的配置。

## 8. 总结

在本文中，我们介绍了发送大小超过1MB的大消息所需的不同Kafka配置。

我们涵盖了生产者、主题、代理和消费者端的配置需求。但是，其中一些是强制性配置，而另一些是可选的。此外，消费者配置是可选的，但最好避免对性能产生负面影响。

最后，我们还介绍了发送大消息的其他可能选项。