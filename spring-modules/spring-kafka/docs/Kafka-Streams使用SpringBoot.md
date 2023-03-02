## 1. 简介

在本文中，我们将了解如何使用Spring Boot设置[Kafka Streams](https://www.baeldung.com/java-kafka-streams)。Kafka Streams是一个建立在Apache Kafka之上的客户端库。它支持以声明性方式处理无限的事件流。

流数据的一些现实示例可以是传感器数据、股票市场事件流和系统日志。在本教程中，我们将构建一个简单的字数统计流应用程序。让我们从Kafka Streams的概述开始，然后在Spring Boot中设置示例及其测试。

## 2. 概述

Kafka Streams提供了Kafka主题和关系型数据库表之间的二元性。它使我们能够对一个或多个流式事件执行连接、分组、聚合和过滤等操作。

Kafka Streams的一个重要概念是处理器拓扑。**处理器拓扑是Kafka Stream对一个或多个事件流进行操作的蓝图**。本质上，处理器拓扑可以被认为是[有向无环图](https://www.baeldung.com/cs/dag-topological-sort)。在此图中，节点分为源节点、处理器节点和接收器节点，而边代表流事件的流向。

拓扑顶部的源接收来自Kafka的流数据，将其向下传递到执行自定义操作的处理器节点，并通过接收器节点流出到新的Kafka主题。除了核心处理之外，还会使用检查点定期保存流的状态，以实现容错和弹性。

## 3. 依赖

我们将从将[spring-kafka](https://central.sonatype.com/artifact/org.springframework.kafka/spring-kafka/3.0.3)和[kafka-streams](https://central.sonatype.com/artifact/org.apache.kafka/kafka-streams/3.4.0)依赖项添加到我们的POM开始：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.7.8</version>
</dependency>
<dependency>
    <groupId>org.apache.kafka</groupId
    <artifactId>kafka-streams</artifactId>
    <version>2.7.1</version>
</dependency>
```

## 4. 示例

我们的示例应用程序从输入Kafka主题中读取流事件。读取记录后，它会处理它们以拆分文本并计算单个单词的数量。随后，它将更新后的字数发送到Kafka输出。除了输出主题之外，我们还将创建一个简单的REST服务来通过HTTP端点公开此计数。

总体而言，输出主题将使用从输入事件中提取的单词及其更新计数不断更新。

### 4.1 配置

首先，让我们在Java配置类中定义Kafka流配置：

```java
@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }

    // other config
}
```

在这里，我们使用了@EnableKafkaStreams注解来自动配置所需的组件。此自动配置需要一个KafkaStreamsConfiguration bean，其名称由DEFAULT_STREAMS_CONFIG_BEAN_NAME指定。因此，**Spring Boot使用此配置并创建一个Kafka Streams客户端来管理我们的应用程序生命周期**。

在我们的示例中，我们为我们的配置提供了应用程序ID、引导服务器连接详细信息和SerDes(序列化器/反序列化器)。

### 4.2 拓扑结构

现在我们已经设置了配置，让我们为我们的应用程序构建拓扑以保持输入消息中的单词计数：

```java
@Component
public class WordCountProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, String> messageStream = streamsBuilder
              .stream("input-topic", Consumed.with(STRING_SERDE, STRING_SERDE));

        KTable<String, Long> wordCounts = messageStream
              .mapValues((ValueMapper<String, String>) String::toLowerCase)
              .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
              .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
              .count();

        wordCounts.toStream().to("output-topic");
    }
}
```

在这里，我们定义了一个配置方法并用@Autowired对其进行标注。Spring处理此注解并将匹配的bean从容器注入到StreamsBuilder参数。或者，我们也可以在配置类中创建一个bean来生成拓扑。

StreamsBuilder使我们能够访问所有Kafka Streams API，它变得像一个常规的Kafka Streams应用程序。在我们的示例中，**我们使用了这个高级DSL来为我们的应用程序定义转换**：

-   使用指定的键和值SerDes从输入主题创建KStream。
-   通过对数据进行转换、拆分、分组、统计，创建一个KTable。
-   将结果具体化为输出流。

本质上，**Spring Boot在管理我们的KStream实例的生命周期的同时，为Streams API提供了一个非常薄的包装器**。它为拓扑创建和配置所需的组件并执行我们的Streams应用程序。重要的是，这让我们专注于我们的核心业务逻辑，而Spring管理生命周期。

### 4.3 Rest服务

在使用声明性步骤定义我们的管道后，让我们创建REST控制器。这提供了端点，以便将消息发布到输入主题并获取指定单词的计数。但重要的是，**应用程序从Kafka Streams状态存储而不是输出主题中检索数据**。

首先，让我们修改之前的KTable并将聚合计数具体化为本地状态存储。然后可以从REST控制器查询：

```java
KTable<String, Long> wordCounts = textStream
    .mapValues((ValueMapper<String, String>) String::toLowerCase)
    .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
    .groupBy((key, value) -> value, Grouped.with(STRING_SERDE, STRING_SERDE))
    .count(Materialized.as("counts"));
```

在此之后，我们可以更新我们的控制器以从这个counts状态存储中检索计数值：

```java
@GetMapping("/count/{word}")
public Long getWordCount(@PathVariable String word) {
    KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
    ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams.store(
        StoreQueryParameters.fromNameAndType("counts", QueryableStoreTypes.keyValueStore())
    );
    return counts.get(word);
}
```

在这里，factoryBean是注入到控制器中的StreamsBuilderFactoryBean实例。这提供了由该工厂bean管理的KafkaStreams实例。因此，我们可以获得我们之前创建的counts键/值状态存储，由KTable表示。此时，我们可以使用它从本地状态存储中获取所请求单词的当前计数。

## 5. 测试

测试是开发和验证我们的应用程序拓扑的关键部分。[Spring Kafka测试库](https://www.baeldung.com/spring-boot-kafka-testing)和[Testcontainers](https://www.baeldung.com/docker-test-containers)都为我们的应用程序在各个级别的测试提供了出色的支持。

### 5.1 单元测试

首先，让我们使用TopologyTestDriver为我们的拓扑设置单元测试。这是用于测试Kafka Streams应用程序的主要测试工具：

```java
@Test
void givenInputMessages_whenProcessed_thenWordCountIsProduced() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    wordCountProcessor.buildPipeline(streamsBuilder);
    Topology topology = streamsBuilder.build();

    try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties())) {
        TestInputTopic<String, String> inputTopic = topologyTestDriver
            .createInputTopic("input-topic", new StringSerializer(), new StringSerializer());
        
        TestOutputTopic<String, Long> outputTopic = topologyTestDriver
            .createOutputTopic("output-topic", new StringDeserializer(), new LongDeserializer());

        inputTopic.pipeInput("key", "hello world");
        inputTopic.pipeInput("key2", "hello");

        assertThat(outputTopic.readKeyValuesToList())
            .containsExactly(
                KeyValue.pair("hello", 1L),
                KeyValue.pair("world", 1L),
                KeyValue.pair("hello", 2L)
            );
    }
}
```

在这里，我们首先需要的是Topology，它封装了我们从WordCountProcessor测试的业务逻辑。现在，我们可以将其与TopologyTestDriver一起使用，为我们的测试创建输入和输出主题。**至关重要的是，这消除了让代理运行并仍然验证管道行为的需要**。换句话说，它可以在不使用真正的Kafka代理的情况下快速轻松地验证我们的管道行为。

### 5.2 集成测试

最后，让我们使用Testcontainers框架来端到端地测试我们的应用程序。这使用正在运行的Kafka代理并启动我们的应用程序以进行完整测试：

```java
@Testcontainers
@SpringBootTest(classes = KafkaStreamsApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class KafkaStreamsApplicationLiveTest {

    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    private final BlockingQueue<String> output = new LinkedBlockingQueue<>();

    // other test setup

    @Test
    void givenInputMessages_whenPostToEndpoint_thenWordCountsReceivedOnOutput() throws Exception {
        postMessage("test message");

        startOutputTopicConsumer();

        // assert correct counts on output topic
        assertThat(output.poll(2, MINUTES)).isEqualTo("test:1");
        assertThat(output.poll(2, MINUTES)).isEqualTo("message:1");

        // assert correct count from REST service
        assertThat(getCountFromRestServiceFor("test")).isEqualTo(1);
        assertThat(getCountFromRestServiceFor("message")).isEqualTo(1);
    }
}
```

在这里，我们向REST控制器发送了一个POST，而REST控制器又将消息发送到Kafka输入主题。作为设置的一部分，我们还启动了一个Kafka消费者。这会异步监听输出的Kafka主题，并使用接收到的字数更新BlockingQueue。

在测试执行期间，应用程序应处理输入消息。接下来，我们可以使用REST服务验证主题和状态存储的预期输出。

## 6. 总结

在本教程中，我们了解了如何创建一个简单的事件驱动应用程序来使用Kafka Streams和Spring Boot处理消息。

在简要概述了核心流概念之后，我们研究了Streams拓扑的配置和创建。然后，我们看到了如何将其与Spring Boot提供的REST功能集成。最后，我们介绍了一些有效测试和验证我们的拓扑和应用程序行为的方法。