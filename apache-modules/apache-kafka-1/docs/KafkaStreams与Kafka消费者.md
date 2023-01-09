## 1. 简介

[Apache Kafka](https://kafka.apache.org/)是最流行的开源分布式容错流处理系统。Kafka Consumer 提供处理消息的基本功能。[Kafka Streams](https://www.baeldung.com/java-kafka-streams) 还在 Kafka Consumer 客户端之上提供实时流处理。

在本教程中，我们将解释 Kafka Streams 的功能，以简化流处理体验。

## 2. Streams 和 Consumer API 的区别

### 2.1. 卡夫卡消费者API

简而言之，[Kafka Consumer API](https://kafka.apache.org/documentation/#consumerapi)允许应用程序处理来自主题的消息。它提供了与它们交互的基本组件，包括以下功能：

-   消费者与生产者责任分离
-   单件加工
-   批处理支持
-   只有无状态的支持。客户端不保留以前的状态并单独评估流中的每个记录
-   写一个应用需要很多代码
-   不使用线程或并行
-   可以写在几个Kafka集群中

[![截图 2021-05-24-at-12.40.45](https://www.baeldung.com/wp-content/uploads/2021/06/Screenshot-2021-05-24-at-12.40.45.png)](https://www.baeldung.com/wp-content/uploads/2021/06/Screenshot-2021-05-24-at-12.40.45.png)

### 2.2. 卡夫卡流 API

[Kafka Streams](https://kafka.apache.org/10/documentation/streams/architecture) 大大简化了主题的流处理。它建立在 Kafka 客户端库之上，提供数据并行、分布式协调、容错和可伸缩性。它将消息作为无界的、连续的、实时的记录流来处理，具有以下特征：

-   单一的 Kafka Stream 消费和生产
-   执行复杂的处理
-   不支持批处理
-   支持无状态和有状态操作
-   编写一个应用程序需要几行代码
-   线程和并行性
-   仅与单个 Kafka 集群交互
-   流分区和任务作为存储和传输消息的逻辑单元

[![截图-2021-05-28-at-10.14.34](https://www.baeldung.com/wp-content/uploads/2021/06/Screenshot-2021-05-28-at-10.14.34-1024x246.png)](https://www.baeldung.com/wp-content/uploads/2021/06/Screenshot-2021-05-28-at-10.14.34.png)

Kafka Streams 使用 分区和任务的概念作为与主题分区紧密相关的逻辑单元。此外，它使用线程在应用程序实例中并行处理。支持的另一个重要功能是状态存储，Kafka Streams 使用它来存储和查询来自主题的数据。最后，Kafka Streams API 与集群交互，但它并不直接在集群之上运行。

在接下来的部分中，我们将重点关注与基本 Kafka 客户端不同的四个方面：流表二元性、Kafka Streams 领域特定语言 (DSL)、Exactly-Once 处理语义 (EOS) 和交互式查询.

### 2.3. 依赖关系

为了实现这些示例，我们只需将[Kafka Consumer API](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.kafka" AND a%3A"kafka-clients") 和[Kafka Streams API](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.kafka" AND a%3A"kafka-streams")依赖项添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.8.0</version>
</dependency>

<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>2.8.0</version>
 </dependency>
```

## 3.流表二元性

Kafka Streams 支持流，但也支持可以双向转换的表。  这就是所谓的[流表二元性](https://www.confluent.io/blog/kafka-streams-tables-part-1-event-streaming/#stream-table-duality)。表格是一组不断变化的事实。每个新事件都会覆盖旧事件，而流是不可变事实的集合。

流处理来自主题的完整数据流。表通过聚合来自流的信息来存储状态。[让我们想象一下像Kafka 数据建模](https://www.baeldung.com/apache-kafka-data-modeling#event-basics)中描述的那样玩国际象棋游戏 。连续移动的流被聚合到一个表中，我们可以从一种状态转换到另一种状态：

[![截图 2021-05-31-at-12.52.43](https://www.baeldung.com/wp-content/uploads/2021/06/Screenshot-2021-05-31-at-12.52.43-1024x585.png)](https://www.baeldung.com/wp-content/uploads/2021/06/Screenshot-2021-05-31-at-12.52.43.png)

### 3.1. KStream , KTable和GlobalKTable

Kafka Streams 为 Streams 和 Tables 提供了两种抽象。KStream处理记录流。另一方面，KTable使用给定键的最新状态管理变更日志流。每条数据记录代表一次更新。

对于非分区表还有另一种抽象。我们可以使用GlobalKTables向所有任务广播信息或在不重新分区输入数据的情况下进行连接。

我们可以将主题读取并反序列化为流：

```java
StreamsBuilder builder = new StreamsBuilder();
KStream<String, String> textLines = 
  builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
```

也可以阅读一个主题来跟踪最近收到的单词作为表格：

```java
KTable<String, String> textLinesTable = 
  builder.table(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
```

最后，我们可以使用全局表读取主题：

```java
GlobalKTable<String, String> textLinesGlobalTable = 
  builder.globalTable(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
```

## 4. 卡夫卡流 DSL

Kafka Streams DSL 是一种声明式和函数式编程风格。它建立在[Streams Processor API](https://kafka.apache.org/documentation/streams/developer-guide/processor-api.html)之上。该语言为上一节中提到的流和表提供了内置抽象。

此外，它还支持无状态(map、filter等)和有状态转换(aggregations、joins和windowing)。因此，只需几行代码就可以实现流处理操作。

### 4.1. 无状态转换

[无状态转换](https://kafka.apache.org/documentation/streams/developer-guide/dsl-api.html#stateless-transformations)不需要处理状态。同样，流处理器中不需要状态存储。示例操作包括filter、map、flatMap或groupBy。

现在让我们看看如何将值映射为大写，从主题中过滤它们并将它们存储为流：

```java
KStream<String, String> textLinesUpperCase =
  textLines
    .map((key, value) -> KeyValue.pair(value, value.toUpperCase()))
    .filter((key, value) -> value.contains("FILTER"));
```

### 4.2. 状态转换

[有状态转换](https://kafka.apache.org/documentation/streams/developer-guide/dsl-api.html#stateful-transformations) 依赖于状态来完成处理操作。一条消息的处理依赖于其他消息的处理(状态存储)。换句话说，任何表或状态存储都可以使用更改日志主题来恢复。

有状态转换的一个例子是字数统计算法：

```java
KTable<String, Long> wordCounts = textLines
  .flatMapValues(value -> Arrays.asList(value
    .toLowerCase(Locale.getDefault()).split("W+")))
  .groupBy((key, word) -> word)
    .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>> as("counts-store"));
```

我们会将这两个字符串发送到主题：

```bash
String TEXT_EXAMPLE_1 = "test test and test";
String TEXT_EXAMPLE_2 = "test filter filter this sentence";

```

结果是：

```bash
Word: and -> 1
Word: test -> 4
Word: filter -> 2
Word: this -> 1
Word: sentence -> 1
```

DSL 涵盖了几个转换特性。我们可以 使用相同的键连接或合并两个输入流/表以生成一个新的流/表。我们还能够将流/表中的多条记录聚合或组合成新表中的一条记录。最后，可以应用windowing，在 join 或 aggregation 函数中对具有相同键的记录进行分组。

一个使用 5s 窗口连接的示例会将按键分组的记录从两个流合并到一个流中：

```java
KStream<String, String> leftRightSource = leftSource.outerJoin(rightSource,
  (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue,
    JoinWindows.of(Duration.ofSeconds(5))).groupByKey()
      .reduce(((key, lastValue) -> lastValue))
  .toStream();
```

所以我们将在左流value=left和key=1中放入，在右流中放入value=right和key=2。结果如下：

```bash
(key= 1) -> (left=left, right=null)
(key= 2) -> (left=null, right=right)
```

对于聚合示例，我们将计算单词计数算法，但使用每个单词的前两个字母作为键：

```java
KTable<String, Long> aggregated = input
  .groupBy((key, value) -> (value != null && value.length() > 0)
    ? value.substring(0, 2).toLowerCase() : "",
    Grouped.with(Serdes.String(), Serdes.String()))
  .aggregate(() -> 0L, (aggKey, newValue, aggValue) -> aggValue + newValue.length(),
    Materialized.with(Serdes.String(), Serdes.Long()));
```

使用以下条目：

```java
"one", "two", "three", "four", "five"

```

输出是：

```bash
Word: on -> 3
Word: tw -> 3
Word: th -> 5
Word: fo -> 4
Word: fi -> 4
```

## 5. 恰好一次处理语义(EOS)

在某些情况下，我们需要确保消费者只阅读一次消息。Kafka 引入了将消息包含到事务中的能力，以使用[Transactional API](https://www.baeldung.com/kafka-exactly-once)实现 EOS 。从 0.11.0 版开始，Kafka Streams 涵盖了相同的功能。

要在 Kafka Streams 中配置 EOS，我们将包含以下属性：

```java
streamsConfiguration.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG,
  StreamsConfig.EXACTLY_ONCE);
```

## 6.交互式查询

[交互式查询](https://kafka.apache.org/documentation/streams/developer-guide/interactive-queries.html)允许在分布式环境中查询应用程序的状态。这意味着能够从本地存储中提取信息，也能从多个实例上的远程存储中提取信息。基本上，我们将收集所有商店并将它们组合在一起以获得应用程序的完整状态。

让我们看一个使用交互式查询的例子。首先，我们将定义处理拓扑，在我们的例子中是字数统计算法：

```java
KStream<String, String> textLines = 
  builder.stream(TEXT_LINES_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

final KGroupedStream<String, String> groupedByWord = textLines
  .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("W+")))
  .groupBy((key, word) -> word, Grouped.with(stringSerde, stringSerde));
```

接下来，我们将为所有计算出的字数创建一个状态存储(键值)：

```java
groupedByWord
  .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("WordCountsStore")
  .withValueSerde(Serdes.Long()));
```

然后，我们可以查询键值存储：

```java
ReadOnlyKeyValueStore<String, Long> keyValueStore =
  streams.store(StoreQueryParameters.fromNameAndType(
    "WordCountsStore", QueryableStoreTypes.keyValueStore()));

KeyValueIterator<String, Long> range = keyValueStore.all();
while (range.hasNext()) {
    KeyValue<String, Long> next = range.next();
    System.out.println("count for " + next.key + ": " + next.value);
}
```

该示例的输出如下：

```bash
Count for and: 1
Count for filter: 2
Count for sentence: 1
Count for test: 4
Count for this: 1
```

## 七. 总结

在本教程中，我们展示了 Kafka Streams 如何简化从 Kafka 主题检索消息时的处理操作。在处理 Kafka 中的流时，它极大地简化了实现。不仅适用于无状态处理，还适用于有状态转换。

当然，不使用 Kafka Streams 也可以完美构建消费者应用程序。但是我们需要手动实施免费提供的一系列额外功能。