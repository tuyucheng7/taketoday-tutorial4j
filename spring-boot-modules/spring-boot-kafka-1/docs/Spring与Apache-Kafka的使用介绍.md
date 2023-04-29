## 1. 概述

[Apache Kafka](https://kafka.apache.org/)是一个分布式和容错的流处理系统。

在本教程中，我们将介绍Spring对Kafka的支持以及它通过原生Kafka Java客户端API提供的抽象级别。

Spring Kafka通过@KafkaListener注解通过KafkaTemplate和消息驱动的POJOs带来了简单典型的Spring模板编程模型。

### 延伸阅读

### [使用Flink和Kafka构建数据管道](https://www.baeldung.com/kafka-flink-data-pipeline)

了解如何使用Flink和Kafka处理流数据

[阅读更多](https://www.baeldung.com/kafka-flink-data-pipeline)→

### [使用MQTT和MongoDB的Kafka连接示例](https://www.baeldung.com/kafka-connect-mqtt-mongodb)

查看使用Kafka连接器的实际示例。

[阅读更多](https://www.baeldung.com/kafka-connect-mqtt-mongodb)→

## 2. 安装与设置

要下载和安装Kafka，请参考[这里](https://kafka.apache.org/quickstart)的官方指南。

我们还需要将spring-kafka依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.7.2</version>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/org.springframework.kafka/spring-kafka/3.0.3)找到此工件的最新版本。

我们的示例应用程序将是一个Spring Boot应用程序。

本文假定服务器使用默认配置启动，并且没有更改服务器端口。

## 3. 配置主题

之前，我们运行命令行工具在Kafka中创建主题：

```shell
$ bin/kafka-topics.sh --create \
  --zookeeper localhost:2181 \
  --replication-factor 1 --partitions 1 \
  --topic mytopic
```

但是随着Kafka中AdminClient的引入，我们现在可以通过编程方式创建主题。

**我们需要添加KafkaAdmin Spring bean，它将自动为所有NewTopic类型的bean添加主题**：

```java
@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic("baeldung", 1, (short) 1);
    }
}
```

## 4. 生产消息

要创建消息，我们首先需要配置一个[ProducerFactory](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/core/ProducerFactory.html)。这设置了创建Kafka[Producer](https://kafka.apache.org/0100/javadoc/org/apache/kafka/clients/producer/Producer.html)实例的策略。

**然后我们需要一个[KafkaTemplate](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/core/KafkaTemplate.html)，它包装了一个Producer实例并提供了向Kafka主题发送消息的便捷方法**。

Producer实例是线程安全的。因此，在整个应用程序上下文中使用单个实例将提供更高的性能。因此，KafkaTemplate实例也是线程安全的，建议使用一个实例。

### 4.1 生产者配置

```java
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
              ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
              bootstrapAddress);
        configProps.put(
              ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
              StringSerializer.class);
        configProps.put(
              ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
              StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

### 4.2 发布消息

我们可以使用KafkaTemplate类发送消息：

```java
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void sendMessage(String msg) {
    kafkaTemplate.send(topicName, msg);
}
```

**send API返回一个ListenableFuture对象**。如果我们想阻塞发送线程并获取发送消息的结果，可以调用ListenableFuture对象的get API。线程会等待结果，但会减慢生产者的速度。

Kafka是一个快速流处理平台。因此，最好异步处理结果，这样后续的消息就不会等待上一个消息的结果。

我们可以通过回调来做到这一点：

```java
public void sendMessage(String message) {
            
    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
	
    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

        @Override
        public void onSuccess(SendResult<String, String> result) {
            System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
        }
        @Override
        public void onFailure(Throwable ex) {
            System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
        }
    });
}
```

## 5. 消费消息

### 5.1 消费者配置

对于消费消息，我们需要配置一个[ConsumerFactory](https://docs.spring.io/autorepo/docs/spring-kafka-dist/1.1.3.RELEASE/api/org/springframework/kafka/core/ConsumerFactory.html)和一个[KafkaListenerContainerFactory](https://docs.spring.io/autorepo/docs/spring-kafka-dist/1.1.3.RELEASE/api/org/springframework/kafka/config/KafkaListenerContainerFactory.html)。一旦这些bean在Spring bean工厂中可用，就可以使用[@KafkaListener](https://docs.spring.io/autorepo/docs/spring-kafka-dist/1.1.3.RELEASE/api/org/springframework/kafka/annotation/KafkaListener.html)注解配置基于POJO的消费者。

**配置类上需要[@EnableKafka](https://docs.spring.io/autorepo/docs/spring-kafka-dist/1.1.3.RELEASE/api/org/springframework/kafka/annotation/EnableKafka.html)注解以启用对Spring管理的beans上的@KafkaListener注解的检测**：

```java
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
              ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
              bootstrapAddress);
        props.put(
              ConsumerConfig.GROUP_ID_CONFIG,
              groupId);
        props.put(
              ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
              StringDeserializer.class);
        props.put(
              ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
              StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```

### 5.2 消费消息

```java
@KafkaListener(topics = "topicName", groupId = "foo")
public void listenGroupFoo(String message) {
    System.out.println("Received Message in group foo: " + message);
}
```

**我们可以为一个主题实现多个监听器**，每个监听器都有不同的groupId。此外，一个消费者可以监听来自不同主题的消息：

```java
@KafkaListener(topics = "topic1, topic2", groupId = "foo")
```

Spring还支持在监听器中使用[@Header](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/messaging/handler/annotation/Header.html)注解检索一个或多个消息头：

```java
@KafkaListener(topics = "topicName")
public void listenWithHeaders(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
      System.out.println("Received Message: " + message" + "from partition: " + partition);
}
```

### 5.3 消费来自特定分区的消息

请注意，我们创建了只有一个分区的主题tuyucheng。

但是，对于具有多个分区的主题，@KafkaListener可以显式订阅具有初始偏移量的主题的特定分区：

```java
@KafkaListener(
    topicPartitions = @TopicPartition(topic = "topicName",
    partitionOffsets = {
        @PartitionOffset(partition = "0", initialOffset = "0"), 
        @PartitionOffset(partition = "3", initialOffset = "0")}),
    containerFactory = "partitionsKafkaListenerContainerFactory")
public void listenToPartition(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
      System.out.println("Received Message: " + message" + "from partition: " + partition);
}
```

由于此监听器中的initialOffset已设置为0，因此每次初始化此监听器时，将重新消费之前来自分区0和3的所有消息。

如果我们不需要设置偏移量，我们可以使用@TopicPartition注解的partitions属性只设置没有偏移量的分区：

```java
@KafkaListener(topicPartitions = @TopicPartition(topic = "topicName", partitions = { "0", "1" }))
```

### 5.4 为监听器添加消息过滤器

我们可以通过添加自定义过滤器来配置监听器以消费特定的消息内容。这可以通过将RecordFilterStrategy设置为[KafkaListenerContainerFactory](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/listener/adapter/RecordFilterStrategy.html)来完成：

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> filterKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setRecordFilterStrategy(record -> record.value().contains("World"));
    return factory;
}
```

然后我们可以配置一个监听器来使用这个容器工厂：

```java
@KafkaListener(
    topics = "topicName", 
    containerFactory = "filterKafkaListenerContainerFactory")
public void listenWithFilter(String message) {
    System.out.println("Received Message in filtered listener: " + message);
}
```

在这个监听器中，**所有匹配过滤器的消息都将被丢弃**。

## 6. 自定义消息转换器

到目前为止，我们只介绍了作为消息发送和接收字符串的内容。但是，我们也可以发送和接收自定义Java对象。这需要在ProducerFactory中配置适当的序列化器，在ConsumerFactory中配置反序列化器。

让我们看一个简单的bean类，我们将其作为消息发送：

```java
public class Greeting {

    private String msg;
    private String name;

    // standard getters, setters and constructor
}
```

### 6.1 生成自定义消息

在此示例中，我们将使用[JsonSerializer](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/support/serializer/JsonSerializer.html)。

让我们看看ProducerFactory和KafkaTemplate的代码：

```java
@Bean
public ProducerFactory<String, Greeting> greetingProducerFactory() {
    // ...
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
        JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
}

@Bean
public KafkaTemplate<String, Greeting> greetingKafkaTemplate() {
    return new KafkaTemplate<>(greetingProducerFactory());
}
```

我们可以使用这个新的KafkaTemplate来发送Greeting消息：

```java
kafkaTemplate.send(topicName, new Greeting("Hello", "World"));
```

### 6.2 消费自定义消息

类似地，让我们修改ConsumerFactory和KafkaListenerContainerFactory以正确反序列化Greeting消息：

```java
@Bean
public ConsumerFactory<String, Greeting> greetingConsumerFactory() {
    // ...
    return new DefaultKafkaConsumerFactory<>(
        props,
        new StringDeserializer(), 
        new JsonDeserializer<>(Greeting.class));
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, Greeting> greetingKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Greeting> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(greetingConsumerFactory());
    return factory;
}
```

spring-kafka JSON序列化器和反序列化器使用[Jackson](https://www.baeldung.com/jackson)库，它也是spring-kafka项目的可选Maven依赖项。

所以，让我们将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.7</version>
</dependency>
```

建议不要使用最新版本的Jackson，而是使用添加到spring-kafka的pom.xml中的版本。

最后，我们需要编写一个监听器来消费Greeting消息：

```java
@KafkaListener(
    topics = "topicName", 
    containerFactory = "greetingKafkaListenerContainerFactory")
public void greetingListener(Greeting greeting) {
    // process greeting message
}
```

## 7. 多方法监听器

现在让我们看看如何配置我们的应用程序以将各种对象发送到同一主题，然后消费它们。

首先，我们将添加一个新类Farewell：

```java
public class Farewell {

    private String message;
    private Integer remainingMinutes;

    // standard getters, setters and constructor
}
```

我们需要一些额外的配置才能将Greeting和Farewell对象发送到同一主题。

### 7.1 在生产者中设置映射类型

在生产者中，我们必须配置[JSON](https://www.baeldung.com/java-json)类型映射：

```java
configProps.put(JsonSerializer.TYPE_MAPPINGS, "greeting:cn.tuyucheng.taketoday.spring.kafka.Greeting, farewell:cn.tuyucheng.taketoday.spring.kafka.Farewell");
```

这样，库将用相应的类名填充type标头。

因此，ProducerFactory和KafkaTemplate看起来像这样：

```java
@Bean
public ProducerFactory<String, Object> multiTypeProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(JsonSerializer.TYPE_MAPPINGS, "greeting:cn.tuyucheng.taketoday.spring.kafka.Greeting, farewell:cn.tuyucheng.taketoday.spring.kafka.Farewell");
    return new DefaultKafkaProducerFactory<>(configProps);
}

@Bean
public KafkaTemplate<String, Object> multiTypeKafkaTemplate() {
    return new KafkaTemplate<>(multiTypeProducerFactory());
}
```

我们可以使用此KafkaTemplate向主题发送Greeting、Farewell或任何[对象](https://www.baeldung.com/java-classes-objects)：

```java
multiTypeKafkaTemplate.send(multiTypeTopicName, new Greeting("Greetings", "World!"));
multiTypeKafkaTemplate.send(multiTypeTopicName, new Farewell("Farewell", 25));
multiTypeKafkaTemplate.send(multiTypeTopicName, "Simple string message");
```

### 7.2 在消费者中使用自定义MessageConverter

为了能够反序列化传入的消息，我们需要为我们的消费者提供一个自定义的MessageConverter。

在幕后，MessageConverter依赖于Jackson2JavaTypeMapper。默认情况下，映射器会推断接收到的对象的类型：相反，我们需要明确地告诉它使用type标头来确定反序列化的目标类：

```java
typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
```

我们还需要提供反向映射信息。在type标头中找到“greeting”标识一个Greeting对象，而“farewell”对应一个Farewell对象：

```java
Map<String, Class<?>> mappings = new HashMap<>(); 
mappings.put("greeting", Greeting.class);
mappings.put("farewell", Farewell.class);
typeMapper.setIdClassMapping(mappings);
```

最后，我们需要配置映射器信任的包。我们必须确保它包含目标类的位置：

```java
typeMapper.addTrustedPackages("cn.tuyucheng.taketoday.spring.kafka");
```

因此，这是此MessageConverter的最终定义：

```java
@Bean
public RecordMessageConverter multiTypeConverter() {
    StringJsonMessageConverter converter = new StringJsonMessageConverter();
    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
    typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
    typeMapper.addTrustedPackages("cn.tuyucheng.taketoday.spring.kafka");
    Map<String, Class<?>> mappings = new HashMap<>();
    mappings.put("greeting", Greeting.class);
    mappings.put("farewell", Farewell.class);
    typeMapper.setIdClassMapping(mappings);
    converter.setTypeMapper(typeMapper);
    return converter;
}
```

我们现在需要告诉我们的ConcurrentKafkaListenerContainerFactory使用MessageConverter和一个相当基本的ConsumerFactory：

```java
@Bean
public ConsumerFactory<String, Object> multiTypeConsumerFactory() {
    HashMap<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props);
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, Object> multiTypeKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(multiTypeConsumerFactory());
    factory.setMessageConverter(multiTypeConverter());
    return factory;
}
```

### 7.3 在监听器中使用@KafkaHandler

最后但同样重要的是，在我们的KafkaListener中，我们将创建一个处理程序方法来检索每个可能的对象。每个处理程序都需要用@KafkaHandler进行标注。

最后一点，让我们指出，我们还可以为无法绑定到Greeting或Farewell类之一的对象定义默认处理程序：

```java
@Component
@KafkaListener(id = "multiGroup", topics = "multitype")
public class MultiTypeKafkaListener {

    @KafkaHandler
    public void handleGreeting(Greeting greeting) {
        System.out.println("Greeting received: " + greeting);
    }

    @KafkaHandler
    public void handleF(Farewell farewell) {
        System.out.println("Farewell received: " + farewell);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        System.out.println("Unkown type received: " + object);
    }
}
```

## 8. 总结

在本文中，我们介绍了Spring对Apache Kafka的支持的基础知识。我们简要地查看了用于发送和接收消息的类。

在运行代码之前，请确保Kafka服务器正在运行并且主题是手动创建的。