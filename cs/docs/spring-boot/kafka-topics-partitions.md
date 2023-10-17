## 1. 简介

在本教程中，我们将探讨[Kafka](https://www.baeldung.com/spring-kafka)主题和分区以及它们之间的相互关系。

## 2.什么是Kafka主题

主题是一系列事件的存储。主题是持久的日志文件，它们按照事件发生的时间顺序保存事件。因此，每个新事件总是添加到日志的末尾。此外，事件是不可变的。因此，在将它们添加到主题后我们无法更改它们。

Kafka 主题的一个示例用例是记录房间的一系列温度测量值。一旦记录了温度值，例如下午 5:02 的 25°C，就无法更改，因为它已经发生了。此外，下午 5:06 的温度值不能早于下午 5:02 记录的温度值。因此，通过将每个温度测量视为一个事件，Kafka 主题将是存储该数据的合适选择。

## 3.什么是Kafka分区

Kafka 使用主题分区来提高可扩展性。在对主题进行分区时，Kafka 将其分解为多个部分，并将每个部分存储在其[分布式系统](https://developer.confluent.io/learn-kafka/apache-kafka/partitions/)的不同节点中。分数的数量由我们或集群默认配置确定。

Kafka保证同一主题的分区之间的事件的顺序。因此，从分区主题消费应该与从没有分区的主题消费相同。

例如，为了提高性能，我们可以将主题分为两个不同的分区，并在消费者端读取它们。在这种情况下，消费者会按照到达主题的相同顺序读取温度，甚至跨分区也是如此。

## 4. 消费者群体

消费者组是从主题读取数据的一组消费者。Kafka 将所有主题分区分配给一个组中的消费者。该划分可能是不平衡的，这意味着可以将多个分区分配给一个使用者。但是，任何给定的分区始终由组中的单个使用者读取。

例如，让我们想象一个具有三个分区的主题，一个具有两个消费者的消费者组应该读取该主题。因此，一种可能的划分是第一个消费者获得分区一和分区二，而另一个消费者仅获得分区三。

Kafka 在后台使用 Zookeeper 在消费者之间划分分区。Zookeper 的重要之处在于它保证了公平的分配。因此，分区在同一组中的消费者之间平均分配。

## 5. 配置应用程序

在本节中，我们将创建类来配置主题、消费者和生产者服务。

### 5.1. 主题配置

首先，让我们为我们的主题创建配置类：


```java
@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    public NewTopic celciusTopic() {
        return TopicBuilder.name("celcius-scale-topic")
                .partitions(1)
                .build();
    }
}
```

KafkaTopicConfig类注入两个 Spring bean。KafkaAdmin bean 使用应运行的网络地址启动 Kafka 集群，而 NewTopic bean创建一个名为celcius -scale-topic的主题， 其中包含一个分区。

### 5.2. 消费者和生产者配置

现在，我们需要必要的类来注入我们主题的生产者和消费者配置。

首先，让我们创建生产者配置类：

```java
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, Double> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DoubleSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Double> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

KafkaProducerConfig注入两个 Spring bean。ProducerFactory告诉 Kafka 应该如何序列化消息以及生产者应该监听哪个服务器。KafkaTemplate 将在消费者服务类中用于创建消息。

### 5.3. 卡夫卡生产者服务

最后，在初始配置之后，我们可以创建驱动程序应用程序。我们首先创建生产者应用程序：

```java
public class ThermostatService {

    private final KafkaTemplate<String, Double> kafkaTemplate;

    public ThermostatService(KafkaTemplate<String, Double> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void measureCelsiusAndPublish(int numMeasurements) {
        new Random().doubles(25, 35)
                .limit(numMeasurements)
                .forEach(tmp -> {
                    kafkaTemplate.send("celcius-scale-topic", tmp);
                });
    }
}
```

ThermostatService包含一个名为measureCelsiusAndPublish的方法。此方法生成 [25, 35] 范围内的随机温度测量值，并发布到celsius-scale-topic Kafka 主题。为了实现这一点，我们使用Random 类的doubles()方法来创建随机数流。然后，我们使用kafkaTemplate的send ()方法 发布消息 。

## 6. 消息的生产和消费

在本节中，我们将了解如何配置 Kafka 使用者以使用嵌入式 Kafka 代理从主题读取消息。

### 6.1. 创建消费者服务

为了消费消息，我们需要一个或多个消费者类。让我们创建一个celcius-scale-topic的消费者：

```java
@Service
public class TemperatureConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    Map<String, Set<String>> consumedRecords = new ConcurrentHashMap<>();

    @KafkaListener(topics = "celcius-scale-topic", groupId = "group-1")
    public void consumer1(ConsumerRecord<?, ?> consumerRecord) {
        computeConsumedRecord("consumer-1", consumerRecord.partition());
    }

    private void computeConsumedRecord(String key, int consumerRecord) {
        consumedRecords.computeIfAbsent(key, k -> new HashSet<>());
        consumedRecords.computeIfPresent(key, (k, v) -> {
            v.add(String.valueOf(consumerRecord));
            return v;
        });
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
```

闩 锁变量是一个线程安全计数器，我们稍后将在集成测试中使用它来确保我们正确接收消息。

我们的consumer1()方法使用@KafkaListener 注释来启动消费者。topic参数是要消费的主题列表，而groupId参数 是消费者所属的消费者组的标识。

为了稍后可视化结果，我们使用 [ConcurrentHashMap ](https://www.baeldung.com/concurrenthashmap-reading-and-writing)来存储消耗的事件。键 对应于消费者的名称，而值包含其消费的分区。

### 6.2. 创建测试类

现在，让我们创建集成测试类：

```java
@SpringBootTest(classes = ThermostatApplicationKafkaApp.class)
@EmbeddedKafka(partitions = 2, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaTopicsAndPartitionsIntegrationTest {
    @ClassRule
    public static EmbeddedKafkaBroker embeddedKafka = new EmbeddedKafkaBroker(1, true, "multitype");

    @Autowired
    private ThermostatService service;

    @Autowired
    private TemperatureConsumer consumer;

    @Test
    public void givenTopic_andConsumerGroup_whenConsumersListenToEvents_thenConsumeItCorrectly() throws Exception {
        service.measureCelsiusAndPublish(10000);
        consumer.getLatch().await(1, TimeUnit.SECONDS);
        System.out.println(consumer.consumedRecords);
    }
}
```

我们使用嵌入式 Kafka 代理来运行[Kafka 测试](https://www.baeldung.com/spring-boot-kafka-testing)。@EmbeddedKafka注释使用参数 brokerProperties来配置代理将在其上运行的URL 和端口。然后，我们使用EmbeddedKafkaBroker 字段中的[JUnit 规则](https://www.baeldung.com/junit-4-rules)启动嵌入式代理。

最后，在测试方法中，我们简单地调用恒温器服务来生成 10,000 条消息。当我们运行该测试时，它会输出类似以下内容的内容：

```bash
{consumer-1=[0, 1]}
```

这意味着同一个消费者处理了分区 0 和 1 中的所有消息，因为我们只有一个消费者和一个消费者组。如果不同消费群体中有更多的消费者，这个结果可能会有所不同。

## 七、总结

在本文中，我们了解了 Kafka 主题和分区的定义以及它们之间的相互关系。

我们还演示了消费者使用嵌入式 Kafka 代理从主题的两个分区读取消息的场景。