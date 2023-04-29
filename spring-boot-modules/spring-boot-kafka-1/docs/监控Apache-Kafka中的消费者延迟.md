## 1. 概述

**Kafka消费者组滞后是任何基于Kafka的事件驱动系统的关键性能指标**。

在本教程中，我们将构建一个分析器应用程序来监控Kafka消费者延迟。

## 2. 消费者滞后

**消费者延迟只是消费者最后提交的偏移量与日志中生产者的最终偏移量之间的差值**。换句话说，消费者滞后测量任何生产者-消费者系统中生产和消费消息之间的延迟。

在本节中，让我们了解如何确定偏移值。

### 2.1 Kafka AdminClient

**要检查消费者组的偏移值，我们需要管理Kafka客户端**。因此，让我们在LagAnalyzerService类中编写一个方法来创建AdminClient类的实例：

```java
private AdminClient getAdminClient(String bootstrapServerConfig) {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
    return AdminClient.create(config);
}
```

我们必须注意使用[@Value](https://www.baeldung.com/spring-value-annotation)注解从属性文件中检索引导服务器列表。同样，我们将使用此注解获取其他值，例如groupId和topicName。

### 2.2 消费组偏移

首先，**我们可以使用AdminClient类的listConsumerGroupOffsets()方法来获取特定消费者组id的offset信息**。

接下来，我们的重点主要放在偏移值上，因此我们可以调用partitionsToOffsetAndMetadata()方法来获取TopicPartition与OffsetAndMetadata值的Map：

```java
private Map<TopicPartition, Long> getConsumerGrpOffsets(String groupId) throws ExecutionException, InterruptedException {
    ListConsumerGroupOffsetsResult info = adminClient.listConsumerGroupOffsets(groupId);
    Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = info.partitionsToOffsetAndMetadata().get();

    Map<TopicPartition, Long> groupOffset = new HashMap<>();
    for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : topicPartitionOffsetAndMetadataMap.entrySet()) {
        TopicPartition key = entry.getKey();
        OffsetAndMetadata metadata = entry.getValue();
        groupOffset.putIfAbsent(new TopicPartition(key.topic(), key.partition()), metadata.offset());
    }
    return groupOffset;
}
```

最后，我们可以注意到对topicPartitionOffsetAndMetadataMap的迭代将我们获取的结果限制为每个主题和分区的偏移值。

### 2.3 生产者偏移

找到消费者组滞后的唯一方法是获取结束偏移值的方法。为此，我们可以使用[KafkaConsumer](https://kafka.apache.org/25/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html)类的endOffsets()方法。

让我们首先在LagAnalyzerService类中创建KafkaConsumer类的实例：

```java
private KafkaConsumer<String, String> getKafkaConsumer(String bootstrapServerConfig) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    return new KafkaConsumer<>(properties);
}
```

接下来，让我们从需要计算滞后的消费者组偏移量中聚合所有相关的TopicPartition值，以便我们将其作为参数提供给endOffsets()方法：

```java
private Map<TopicPartition, Long> getProducerOffsets(Map<TopicPartition, Long> consumerGrpOffset) {
    List<TopicPartition> topicPartitions = new LinkedList<>();
    for (Map.Entry<TopicPartition, Long> entry : consumerGrpOffset.entrySet()) {
        TopicPartition key = entry.getKey();
        topicPartitions.add(new TopicPartition(key.topic(), key.partition()));
    }
    return kafkaConsumer.endOffsets(topicPartitions);
}
```

最后，让我们编写一个方法，使用消费者偏移量和生产者端偏移量为每个TopicPartition生成滞后：

```java
private Map<TopicPartition, Long> computeLags(Map<TopicPartition, Long> consumerGrpOffsets, Map<TopicPartition, Long> producerOffsets) {
    Map<TopicPartition, Long> lags = new HashMap<>();
    for (Map.Entry<TopicPartition, Long> entry : consumerGrpOffsets.entrySet()) {
        Long producerOffset = producerOffsets.get(entry.getKey());
        Long consumerOffset = consumerGrpOffsets.get(entry.getKey());
        long lag = Math.abs(producerOffset - consumerOffset);
        lags.putIfAbsent(entry.getKey(), lag);
    }
    return lags;
}
```

## 3. 滞后分析器

现在，让我们通过在LagAnalyzerService类中编写analyzeLag()方法来编排滞后分析：

```java
public void analyzeLag(String groupId) throws ExecutionException, InterruptedException {
    Map<TopicPartition, Long> consumerGrpOffsets = getConsumerGrpOffsets(groupId);
    Map<TopicPartition, Long> producerOffsets = getProducerOffsets(consumerGrpOffsets);
    Map<TopicPartition, Long> lags = computeLags(consumerGrpOffsets, producerOffsets);
    for (Map.Entry<TopicPartition, Long> lagEntry : lags.entrySet()) {
        String topic = lagEntry.getKey().topic();
        int partition = lagEntry.getKey().partition();
        Long lag = lagEntry.getValue();
        System.out.printf("Time=%s | Lag for topic = %s, partition = %s is %d\n",
            MonitoringUtil.time(),
            topic,
            partition,
            lag);
    }
}
```

然而，当谈到监控滞后指标时，**我们需要一个几乎实时的滞后值，以便我们可以采取任何管理措施来恢复系统性能**。

实现此目的的一种直接方法是**定期轮询滞后值**。因此，让我们创建一个LiveLagAnalyzerService服务，该服务将调用LagAnalyzerService的analyzeLag()方法：

```java
@Scheduled(fixedDelay = 5000L)
public void liveLagAnalysis() throws ExecutionException, InterruptedException {
    lagAnalyzerService.analyzeLag(groupId);
}
```

出于我们的目的，我们使用[@Scheduled](https://www.baeldung.com/spring-scheduled-tasks)注解将轮询频率设置为5秒。但是，对于实时监控，我们可能需要通过[JMX](https://www.baeldung.com/java-management-extensions)访问它。

## 4. 模拟

在本节中，我们将**模拟[本地Kafka设置](https://www.baeldung.com/ops/kafka-docker-setup)的Kafka生产者和消费者**，以便我们可以在不依赖外部Kafka生产者和消费者的情况下看到LagAnalyzer的运行情况。

### 4.1 模拟模式

由于模拟模式仅用于演示目的，因此当我们想要针对真实场景运行滞后分析器应用程序时，我们应该有一种机制将其关闭。

我们可以将其保留为application.properties资源文件中的可配置属性：

```properties
monitor.producer.simulate=true
monitor.consumer.simulate=true
```

我们会将这些属性插入Kafka生产者和消费者并控制它们的行为。

此外，让我们定义生产者startTime、endTime和辅助方法time()以在监控期间获取当前时间：

```java
public static final Date startTime = new Date();
public static final Date endTime = new Date(startTime.getTime() + 30 * 1000);

public static String time() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String date = dtf.format(now);
    return date;
}
```

### 4.2 生产者-消费者配置

我们需要定义一些核心配置值来为我们的Kafka消费者和生产者模拟器实例化实例。

首先，让我们在KafkaConsumerConfig类中定义消费者模拟器的配置：

```java
public ConsumerFactory<String, String> consumerFactory(String groupId) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    if (enabled) {
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    } else {
        props.put(ConsumerConfig.GROUP_ID_CONFIG, simulateGroupId);
    }
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 0);
    return new DefaultKafkaConsumerFactory<>(props);
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    if (enabled) {
        factory.setConsumerFactory(consumerFactory(groupId));
    } else {
        factory.setConsumerFactory(consumerFactory(simulateGroupId));
    }
    return factory;
}

```

接下来，我们可以在KafkaProducerConfig类中定义生产者模拟器的配置：

```java
@Bean
public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
}

@Bean
public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
}
```

此外，让我们使用[@KafkaListener](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/annotation/KafkaListener.html)注解来指定目标监听器，当然，只有当monitor.consumer.simulate设置为true时才会启用该监听器：

```java
@KafkaListener(
    topics = "${monitor.topic.name}",
    containerFactory = "kafkaListenerContainerFactory",
    autoStartup = "${monitor.consumer.simulate}")
public void listen(String message) throws InterruptedException {
    Thread.sleep(10L);
}
```

因此，我们添加了10毫秒的休眠时间来制造人为的消费者延迟。

最后，**让我们编写一个sendMessage()方法来模拟生产者**：

```java
@Scheduled(fixedDelay = 1L, initialDelay = 5L)
public void sendMessage() throws ExecutionException, InterruptedException {
    if (enabled) {
        if (endTime.after(new Date())) {
            String message = "msg-" + time();
            SendResult<String, String> result = kafkaTemplate.send(topicName, message).get();
        }
    }
}
```

我们可以注意到，生产者将以1条消息/毫秒的速率生成消息。此外，它会在模拟开始时间后30秒的结束时间后停止生成消息。

### 4.3 实时监控

现在，让我们在LagAnalyzerApplication中运行main方法：

```java
public static void main(String[] args) {
    SpringApplication.run(LagAnalyzerApplication.class, args);
    while (true) ;
}
```

每30秒后，我们将在主题的每个分区上看到当前滞后：

```shell
Time=2021/06/06 11:07:24 | Lag for topic = baeldungTopic, partition = 0 is 93
Time=2021/06/06 11:07:29 | Lag for topic = baeldungTopic, partition = 0 is 290
Time=2021/06/06 11:07:34 | Lag for topic = baeldungTopic, partition = 0 is 776
Time=2021/06/06 11:07:39 | Lag for topic = baeldungTopic, partition = 0 is 1159
Time=2021/06/06 11:07:44 | Lag for topic = baeldungTopic, partition = 0 is 1559
Time=2021/06/06 11:07:49 | Lag for topic = baeldungTopic, partition = 0 is 2015
Time=2021/06/06 11:07:54 | Lag for topic = baeldungTopic, partition = 0 is 1231
Time=2021/06/06 11:07:59 | Lag for topic = baeldungTopic, partition = 0 is 731
Time=2021/06/06 11:08:04 | Lag for topic = baeldungTopic, partition = 0 is 231
Time=2021/06/06 11:08:09 | Lag for topic = baeldungTopic, partition = 0 is 0
```

因此，生产者生成消息的速率为1条消息/毫秒，高于消费者消费消息的速率。因此，**滞后将在前30秒内开始增加，之后生产者停止生产，因此滞后将逐渐下降到0**。

## 5. 总结

在本教程中，我们了解了如何找到Kafka主题上的消费者延迟。此外，我们利用这些知识在Spring中创建了一个LagAnalyzer应用程序，该应用程序几乎可以实时显示延迟。