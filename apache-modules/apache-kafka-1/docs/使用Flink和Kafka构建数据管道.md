## 1. 概述

[Apache Flink](https://flink.apache.org/)是一个流处理框架，可以轻松地与Java一起使用。[Apache Kafka](https://kafka.apache.org/)是一个支持高容错的分布式流处理系统。

在本教程中，我们将了解如何使用这两种技术构建数据管道。

## 2. 安装

安装配置Apache Kafka，请参考[官方指南](https://kafka.apache.org/quickstart)。安装后，我们可以使用以下命令创建名为 flink_input 和flink_output 的新主题：

```plaintext
 bin/kafka-topics.sh --create 
  --zookeeper localhost:2181 
  --replication-factor 1 --partitions 1 
  --topic flink_output

 bin/kafka-topics.sh --create 
  --zookeeper localhost:2181 
  --replication-factor 1 --partitions 1 
  --topic flink_input
```

在本教程中，我们将使用 Apache Kafka 的默认配置和默认端口。

## 3. Flink 使用

Apache Flink 允许实时流处理技术。该框架允许使用多个第三方系统作为流源或接收器。

在 Flink 中——有各种可用的连接器：

-   Apache Kafka(源/接收器)
-   Apache Cassandra(接收器)
-   Amazon Kinesis Streams(源/接收器)
-   弹性搜索(接收器)
-   Hadoop 文件系统(接收器)
-   RabbitMQ(源/接收器)
-   Apache NiFi(源/接收器)
-   Twitter Streaming API(来源)

要将 Flink 添加到我们的项目中，我们需要包含以下 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-core</artifactId>
    <version>1.5.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka-0.11_2.11</artifactId>
    <version>1.5.0</version>
</dependency>
```

添加这些依赖项将使我们能够消费和生产 Kafka 主题。[你可以在Maven Central](https://search.maven.org/search?q=g:org.apache.flink)上找到 Flink 的当前版本。

## 4. Kafka 字符串消费者

要使用 Flink 使用来自 Kafka 的数据，我们需要提供一个主题和一个 Kafka 地址。我们还应该提供一个组 ID，用于保存偏移量，这样我们就不会总是从头读取整个数据。

让我们创建一个静态方法，使 FlinkKafkaConsumer 的创建更容易：

```java
public static FlinkKafkaConsumer011<String> createStringConsumerForTopic(
  String topic, String kafkaAddress, String kafkaGroup ) {
 
    Properties props = new Properties();
    props.setProperty("bootstrap.servers", kafkaAddress);
    props.setProperty("group.id",kafkaGroup);
    FlinkKafkaConsumer011<String> consumer = new FlinkKafkaConsumer011<>(
      topic, new SimpleStringSchema(), props);

    return consumer;
}
```

此方法采用 主题、kafkaAddress和kafkaGroup，并创建 FlinkKafkaConsumer，它将使用来自给定主题的数据作为字符串，因为我们使用 SimpleStringSchema来解码数据。

类名中的数字011指的是Kafka版本。

## 5. Kafka 字符串生成器

要向 Kafka 生成数据，我们需要提供我们要使用的 Kafka 地址和主题。同样，我们可以创建一个静态方法来帮助我们为不同的主题创建生产者：

```java
public static FlinkKafkaProducer011<String> createStringProducer(
  String topic, String kafkaAddress){

    return new FlinkKafkaProducer011<>(kafkaAddress,
      topic, new SimpleStringSchema());
}
```

此方法仅将 主题和 kafkaAddress作为参数，因为在生成 Kafka 主题时无需提供组 ID。

## 6. 字符串流处理

当我们有一个完全工作的消费者和生产者时，我们可以尝试处理来自 Kafka 的数据，然后将我们的结果保存回 Kafka。[可以在此处](https://www.baeldung.com/apache-flink)找到可用于流处理的完整函数列表。

在这个例子中，我们将把每个 Kafka 条目中的单词大写，然后将其写回 Kafka。

为此，我们需要创建一个自定义 MapFunction：

```java
public class WordsCapitalizer implements MapFunction<String, String> {
    @Override
    public String map(String s) {
        return s.toUpperCase();
    }
}
```

创建函数后，我们可以在流处理中使用它：

```java
public static void capitalize() {
    String inputTopic = "flink_input";
    String outputTopic = "flink_output";
    String consumerGroup = "baeldung";
    String address = "localhost:9092";
    StreamExecutionEnvironment environment = StreamExecutionEnvironment
      .getExecutionEnvironment();
    FlinkKafkaConsumer011<String> flinkKafkaConsumer = createStringConsumerForTopic(
      inputTopic, address, consumerGroup);
    DataStream<String> stringInputStream = environment
      .addSource(flinkKafkaConsumer);

    FlinkKafkaProducer011<String> flinkKafkaProducer = createStringProducer(
      outputTopic, address);

    stringInputStream
      .map(new WordsCapitalizer())
      .addSink(flinkKafkaProducer);
}
```

应用程序将从flink_input主题读取数据，对流执行操作，然后将结果保存到 Kafka 中的flink_output 主题。

我们已经了解了如何使用 Flink 和 Kafka 处理字符串。但通常需要对自定义对象执行操作。我们将在下一章中看到如何做到这一点。

## 7.自定义对象反序列化

以下类表示包含有关发件人和收件人信息的简单消息：

```java
@JsonSerialize
public class InputMessage {
    String sender;
    String recipient;
    LocalDateTime sentAt;
    String message;
}
```

之前我们是使用 SimpleStringSchema反序列化来自 Kafka 的消息，但是现在我们想直接反序列化数据到自定义对象。

为此，我们需要一个自定义的DeserializationSchema：

```java
public class InputMessageDeserializationSchema implements
  DeserializationSchema<InputMessage> {

    static ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule());

    @Override
    public InputMessage deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, InputMessage.class);
    }

    @Override
    public boolean isEndOfStream(InputMessage inputMessage) {
        return false;
    }

    @Override
    public TypeInformation<InputMessage> getProducedType() {
        return TypeInformation.of(InputMessage.class);
    }
}
```

我们在这里假设消息在 Kafka 中以 JSON 格式保存。

由于我们有一个LocalDateTime类型的字段，我们需要指定 JavaTimeModule， 它负责将LocalDateTime对象映射到 JSON。

Flink 模式不能有不可序列化的字段，因为所有运算符(如模式或函数)都在作业开始时被序列化。

Apache Spark 中也存在类似的问题。此问题的已知修复方法之一是将字段初始化为static，就像我们在上面对ObjectMapper所做的那样。它不是最漂亮的解决方案，但它相对简单并且可以完成工作。

isEndOfStream方法 可用于特殊情况，即仅应在接收到某些特定数据之前处理流。但在我们的案例中不需要它。

## 8.自定义对象序列化

现在，假设我们希望我们的系统能够创建消息备份。我们希望这个过程是自动的，每个备份应该由一整天发送的消息组成。

此外，备份消息应分配有唯一 ID。

为此，我们可以创建以下类：

```java
public class Backup {
    @JsonProperty("inputMessages")
    List<InputMessage> inputMessages;
    @JsonProperty("backupTimestamp")
    LocalDateTime backupTimestamp;
    @JsonProperty("uuid")
    UUID uuid;

    public Backup(List<InputMessage> inputMessages, 
      LocalDateTime backupTimestamp) {
        this.inputMessages = inputMessages;
        this.backupTimestamp = backupTimestamp;
        this.uuid = UUID.randomUUID();
    }
}
```

请注意 UUID 生成机制并不完美，因为它允许重复。但是，这对于本示例的范围来说已经足够了。

我们想将我们的 备份 对象作为 JSON 保存到 Kafka，所以我们需要创建我们的SerializationSchema：

```java
public class BackupSerializationSchema
  implements SerializationSchema<Backup> {

    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(BackupSerializationSchema.class);

    @Override
    public byte[] serialize(Backup backupMessage) {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper()
              .registerModule(new JavaTimeModule());
        }
        try {
            return objectMapper.writeValueAsString(backupMessage).getBytes();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }
        return new byte[0];
    }
}
```

## 9.时间戳消息

由于我们要为每天的所有消息创建备份，因此消息需要一个时间戳。

Flink 提供了三种不同的时间特性 EventTime、ProcessingTime 和 IngestionTime 。

在我们的例子中，我们需要使用消息发送的时间，因此我们将使用 EventTime。

要使用 EventTime ，我们需要一个 TimestampAssigner，它将从我们的输入数据中提取时间戳：

```java
public class InputMessageTimestampAssigner 
  implements AssignerWithPunctuatedWatermarks<InputMessage> {
 
    @Override
    public long extractTimestamp(InputMessage element, 
      long previousElementTimestamp) {
        ZoneId zoneId = ZoneId.systemDefault();
        return element.getSentAt().atZone(zoneId).toEpochSecond()  1000;
    }

    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(InputMessage lastElement, 
      long extractedTimestamp) {
        return new Watermark(extractedTimestamp - 1500);
    }
}
```

我们需要将 LocalDateTime转换为EpochSecond，因为这是 Flink 期望的格式。分配时间戳后，所有基于时间的操作都将使用 sentAt字段中的时间来操作。

由于 Flink 期望时间戳以毫秒为单位，而 toEpochSecond() 以秒为单位返回时间，我们需要将其乘以 1000，因此 Flink 将正确创建窗口。

Flink 定义了 Watermark 的概念。 水印在数据未按发送顺序到达的情况下很有用。水印定义允许处理元素的最大延迟。

时间戳低于水印的元素根本不会被处理。

## 10. 创建时间窗口

为了确保我们的备份只收集一天内发送的消息，我们可以在流上使用timeWindowAll方法，它将消息拆分为窗口。

但是，我们仍然需要聚合来自每个窗口的消息并将它们作为 备份返回。

为此，我们需要一个自定义 AggregateFunction：

```java
public class BackupAggregator 
  implements AggregateFunction<InputMessage, List<InputMessage>, Backup> {
 
    @Override
    public List<InputMessage> createAccumulator() {
        return new ArrayList<>();
    }

    @Override
    public List<InputMessage> add(
      InputMessage inputMessage,
      List<InputMessage> inputMessages) {
        inputMessages.add(inputMessage);
        return inputMessages;
    }

    @Override
    public Backup getResult(List<InputMessage> inputMessages) {
        return new Backup(inputMessages, LocalDateTime.now());
    }

    @Override
    public List<InputMessage> merge(List<InputMessage> inputMessages,
      List<InputMessage> acc1) {
        inputMessages.addAll(acc1);
        return inputMessages;
    }
}
```

## 11.聚合备份

在分配适当的时间戳并实现我们的 AggregateFunction之后，我们终于可以获取我们的 Kafka 输入并对其进行处理：

```java
public static void createBackup () throws Exception {
    String inputTopic = "flink_input";
    String outputTopic = "flink_output";
    String consumerGroup = "baeldung";
    String kafkaAddress = "192.168.99.100:9092";
    StreamExecutionEnvironment environment
      = StreamExecutionEnvironment.getExecutionEnvironment();
    environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    FlinkKafkaConsumer011<InputMessage> flinkKafkaConsumer
      = createInputMessageConsumer(inputTopic, kafkaAddress, consumerGroup);
    flinkKafkaConsumer.setStartFromEarliest();

    flinkKafkaConsumer.assignTimestampsAndWatermarks(
      new InputMessageTimestampAssigner());
    FlinkKafkaProducer011<Backup> flinkKafkaProducer
      = createBackupProducer(outputTopic, kafkaAddress);

    DataStream<InputMessage> inputMessagesStream
      = environment.addSource(flinkKafkaConsumer);

    inputMessagesStream
      .timeWindowAll(Time.hours(24))
      .aggregate(new BackupAggregator())
      .addSink(flinkKafkaProducer);

    environment.execute();
}
```

## 12.总结

在本文中，我们介绍了如何使用 Apache Flink 和 Apache Kafka 创建一个简单的数据管道。