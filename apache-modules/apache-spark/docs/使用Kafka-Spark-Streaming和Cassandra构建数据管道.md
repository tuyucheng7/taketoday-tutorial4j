## 1. 概述

[Apache Kafka](https://kafka.apache.org/)是一个可扩展、高性能、低延迟的平台，允许像消息系统一样读取和写入数据流。 我们可以很容易地[从Java中的 Kafka 入手。](https://www.baeldung.com/spring-kafka)

[Spark Streaming](https://spark.apache.org/streaming/)是 [Apache Spark](https://spark.apache.org/)平台的一部分，它支持可扩展、高吞吐量、容错的数据流处理。虽然是用 Scala 编写的，但[Spark 提供了JavaAPI 来与](https://www.baeldung.com/apache-spark).

[Apache Cassandra](https://cassandra.apache.org/) 是一个 分布式的宽列 NoSQL 数据存储。[有关 Cassandra](https://www.baeldung.com/cassandra-with-java)的更多详细信息，请参阅我们之前的文章。

在本教程中，我们将结合这些来为实时数据流创建 高度可扩展和容错的数据管道。

## 延伸阅读：

## [使用 Cassandra、Astra 和 Stargate 构建仪表板](https://www.baeldung.com/cassandra-astra-stargate-dashboard)

了解如何使用 DataStax Astra 构建仪表板，DataStax Astra 是一种由 Apache Cassandra 和 Stargate API 提供支持的数据库即服务。

[阅读更多](https://www.baeldung.com/cassandra-astra-stargate-dashboard)→

## [使用 Cassandra、Astra、REST 和 GraphQL 构建仪表板——记录状态更新](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)

使用 Cassandra 存储时间序列数据的示例。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)→

## [使用 Cassandra、Astra 和 CQL 构建仪表板——映射事件数据](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)

了解如何根据存储在 Astra 数据库中的数据在交互式地图上显示事件。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)→



## 2. 安装

首先，我们需要在我们的机器上本地安装 Kafka、Spark 和 Cassandra 来运行应用程序。我们将看到如何使用这些平台开发数据管道。

但是，我们将保留所有默认配置，包括所有安装的端口，这将有助于让教程顺利运行。

### 2.1. 卡夫卡

在本地机器上安装 Kafka 非常简单，可以[在官方文档](https://kafka.apache.org/quickstart)中找到。我们将使用 Kafka 的 2.1.0 版本。

此外，Kafka 需要[Apache Zookeeper](https://zookeeper.apache.org/)才能运行，但出于本教程的目的，我们将利用 Kafka 打包的单节点 Zookeeper 实例。

一旦我们成功按照官方指南在本地启动了 Zookeeper 和 Kafka，我们就可以继续创建名为“messages”的主题：

```powershell
 $KAFKA_HOME$binwindowskafka-topics.bat --create 
  --zookeeper localhost:2181 
  --replication-factor 1 --partitions 1 
  --topic messages
```

请注意，上面的脚本适用于 Windows 平台，但也有类似的脚本适用于类 Unix 平台。

### 2.2. 火花

Spark 将 Hadoop 的客户端库用于 HDFS 和 YARN。因此，组装所有这些的兼容版本可能非常棘手。但是，[官方下载的 Spark](https://spark.apache.org/downloads.html)预打包了流行版本的 Hadoop。对于本教程，我们将使用“为 Apache Hadoop 2.7 及更高版本预构建”的 2.3.0 版本包。

一旦解压了正确的 Spark 包，就可以使用可用的脚本来提交应用程序。我们稍后在Spring Boot中开发应用程序时会看到这一点。

### 2.3. 卡桑德拉

DataStax 为包括 Windows 在内的不同平台提供了 Cassandra 社区版。[我们可以按照官方文档](https://academy.datastax.com/planet-cassandra//cassandra)很容易地在本地机器上下载并安装它。我们将使用 3.9.0 版。

一旦我们设法在我们的本地机器上安装并启动 Cassandra，我们就可以继续创建我们的键空间和表。这可以使用我们的安装附带的 CQL Shell 来完成：

```sql
CREATE KEYSPACE vocabulary
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
    };
USE vocabulary;
CREATE TABLE words (word text PRIMARY KEY, count int);
```

请注意，我们已经创建了一个名为vocabulary的名称空间和一个名为words的表，其中包含两列，word和count。

## 3.依赖关系

我们可以通过 Maven 将 Kafka 和 Spark 依赖项集成到我们的应用程序中。我们将从 Maven Central 中提取这些依赖项：

-   [核心火花](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.spark" AND a%3A"spark-core_2.11")
-   [星火](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.spark" AND a%3A"spark-sql_2.11")
-   [流式火花](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.spark" AND a%3A"spark-streaming_2.11")
-   [流式传输 Kafka Spark](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.spark" AND a%3A"spark-streaming-kafka-0-10_2.11")
-   [卡桑德拉星火](https://search.maven.org/classic/#search|gav|1|g%3A"com.datastax.spark" AND a%3A"spark-cassandra-connector_2.11")
-   [卡桑德拉Java火花](https://search.maven.org/classic/#search|gav|1|g%3A"com.datastax.spark" AND a%3A"spark-cassandra-connector-java_2.11")

我们可以相应地将它们添加到我们的 pom 中：

```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.11</artifactId>
    <version>2.3.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.11</artifactId>
    <version>2.3.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming_2.11</artifactId>
    <version>2.3.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming-kafka-0-10_2.11</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.datastax.spark</groupId>
    <artifactId>spark-cassandra-connector_2.11</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.datastax.spark</groupId>
    <artifactId>spark-cassandra-connector-java_2.11</artifactId>
    <version>1.5.2</version>
</dependency>
```

请注意，其中一些依赖项被标记为在范围内提供。这是因为这些将由 Spark 安装提供，我们将在其中使用 spark-submit 提交应用程序以供执行。

## 4. Spark Streaming – Kafka 集成策略

至此，有必要简单谈谈Spark和Kafka的集成策略。

Kafka 在 0.8 和 0.10 版本之间引入了新的消费者 API。因此，相应的 Spark Streaming 包可用于两个代理版本。根据可用的经纪人和所需的功能选择正确的软件包很重要。

### 4.1. 星火流卡夫卡 0.8

0.8 版本是稳定的集成 API ，可选择使用 Receiver-based 或 Direct Approach。我们不会详细介绍[可以在官方文档中找到](https://spark.apache.org/docs/2.2.0/streaming-kafka-0-8-integration.html)的这些方法。这里需要注意的重要一点是，此包与 Kafka Broker 版本 0.8.2.1 或更高版本兼容。

### 4.2. 星火流卡夫卡 0.10

目前处于实验状态，仅与 Kafka Broker 0.10.0 或更高版本兼容。该软件包 仅提供直接方法，现在使用新的 Kafka 消费者 API。我们可以[在官方文档](https://spark.apache.org/docs/2.2.0/streaming-kafka-0-10-integration.html)中找到有关此的更多详细信息。重要的是，它不向后兼容旧的 Kafka Broker 版本。

请注意，对于本教程，我们将使用 0.10 包。上一节中提到的依赖仅指此。

## 5. 开发数据管道

我们将使用 Spark 在Java中创建一个简单的应用程序，它将与我们之前创建的 Kafka 主题集成。该应用程序将读取发布的消息并计算每条消息中单词的出现频率。这将在我们之前创建的 Cassandra 表中更新。

让我们快速想象一下数据将如何流动：

[![简单的数据管道 1](https://www.baeldung.com/wp-content/uploads/2019/01/Simple-Data-Pipeline-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/01/Simple-Data-Pipeline-1.jpg)

 

### 5.1. 获取JavaStreamingContext

首先，我们将从初始化JavaStreamingContext开始，它是所有 Spark Streaming 应用程序的入口点：

```java
SparkConf sparkConf = new SparkConf();
sparkConf.setAppName("WordCountingApp");
sparkConf.set("spark.cassandra.connection.host", "127.0.0.1");

JavaStreamingContext streamingContext = new JavaStreamingContext(
  sparkConf, Durations.seconds(1));
```

### 5.2. 从 Kafka获取DStream

现在，我们可以从JavaStreamingContext连接到 Kafka 主题：

```java
Map<String, Object> kafkaParams = new HashMap<>();
kafkaParams.put("bootstrap.servers", "localhost:9092");
kafkaParams.put("key.deserializer", StringDeserializer.class);
kafkaParams.put("value.deserializer", StringDeserializer.class);
kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
kafkaParams.put("auto.offset.reset", "latest");
kafkaParams.put("enable.auto.commit", false);
Collection<String> topics = Arrays.asList("messages");

JavaInputDStream<ConsumerRecord<String, String>> messages = 
  KafkaUtils.createDirectStream(
    streamingContext, 
    LocationStrategies.PreferConsistent(), 
    ConsumerStrategies.<String, String> Subscribe(topics, kafkaParams));
```

请注意，我们必须在此处为键和值提供反序列化器。对于String等常见数据类型，反序列化器默认可用。但是，如果我们希望检索自定义数据类型，则必须提供自定义反序列化器。

在这里，我们获得了JavaInputDStream，它是 Discretized Streams 或DStreams 的实现，是 Spark Streaming 提供的基本抽象。在内部，DStreams 只不过是一系列连续的 RDD。

### 5.3. 处理获得 的DStream

我们现在将对JavaInputDStream执行一系列操作以获取消息中的词频：

```java
JavaPairDStream<String, String> results = messages
  .mapToPair( 
      record -> new Tuple2<>(record.key(), record.value())
  );
JavaDStream<String> lines = results
  .map(
      tuple2 -> tuple2._2()
  );
JavaDStream<String> words = lines
  .flatMap(
      x -> Arrays.asList(x.split("s+")).iterator()
  );
JavaPairDStream<String, Integer> wordCounts = words
  .mapToPair(
      s -> new Tuple2<>(s, 1)
  ).reduceByKey(
      (i1, i2) -> i1 + i2
    );
```

### 5.4. 将处理 后的 DStream持久化 到 Cassandra

最后，我们可以迭代处理过的JavaPairDStream以将它们插入到我们的 Cassandra 表中：

```java
wordCounts.foreachRDD(
    javaRdd -> {
      Map<String, Integer> wordCountMap = javaRdd.collectAsMap();
      for (String key : wordCountMap.keySet()) {
        List<Word> wordList = Arrays.asList(new Word(key, wordCountMap.get(key)));
        JavaRDD<Word> rdd = streamingContext.sparkContext().parallelize(wordList);
        javaFunctions(rdd).writerBuilder(
          "vocabulary", "words", mapToRow(Word.class)).saveToCassandra();
      }
    }
  );
```

### 5.5. 运行应用程序

由于这是一个流处理应用程序，我们希望保持它运行：

```java
streamingContext.start();
streamingContext.awaitTermination();
```

## 6. 利用检查点

在流处理应用程序中，保留正在处理的数据批次之间的状态通常很有用。

例如，在我们之前的尝试中，我们只能存储单词的当前频率。如果我们想存储累积频率怎么办？Spark Streaming 通过称为检查点的概念使其成为可能。

我们现在将修改我们之前创建的管道以利用检查点：

[![带检查点的数据管道 1](https://www.baeldung.com/wp-content/uploads/2019/01/Data-Pipeline-With-Checkpoints-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/01/Data-Pipeline-With-Checkpoints-1.jpg)

请注意，我们将仅在数据处理会话中使用检查点。这不提供容错。然而，检查点也可以用于容错。

我们必须在应用程序中进行一些更改才能利用检查点。这包括为JavaStreamingContext 提供检查点位置：

```java
streamingContext.checkpoint("./.checkpoint");
```

在这里，我们使用本地文件系统来存储检查点。但是，为了稳健性，这应该存储在 HDFS、S3 或 Kafka 等位置。有关更多信息，请参见[官方文档](https://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#checkpointing)。

接下来，我们必须在使用映射函数处理每个分区时获取检查点并创建单词的累积计数：

```java
JavaMapWithStateDStream<String, Integer, Integer, Tuple2<String, Integer>> cumulativeWordCounts = wordCounts
  .mapWithState(
    StateSpec.function( 
        (word, one, state) -> {
          int sum = one.orElse(0) + (state.exists() ? state.get() : 0);
          Tuple2<String, Integer> output = new Tuple2<>(word, sum);
          state.update(sum);
          return output;
        }
      )
    );
```

一旦我们获得了累积的字数，我们就可以像以前一样继续迭代并将它们保存在 Cassandra 中。

请注意，虽然数据检查点对有状态处理很有用，但它会带来延迟成本。因此，有必要明智地使用它以及最佳检查点间隔。

## 7. 理解抵消

如果我们回忆一下我们之前设置的一些 Kafka 参数：

```java
kafkaParams.put("auto.offset.reset", "latest");
kafkaParams.put("enable.auto.commit", false);
```

这些基本上意味着我们不想自动提交偏移量，而是希望在每次初始化消费者组时都选择最新的偏移量。因此，我们的应用程序将只能使用在其运行期间发布的消息。

如果我们想使用所有发布的消息而不管应用程序是否正在运行，并且还想跟踪已经发布的消息，我们必须适当地配置偏移量并保存偏移量状态， 尽管这有点超出本教程的范围。

这也是 Spark Streaming 提供特定级别保证(如“恰好一次”)的一种方式。这基本上意味着发布在 Kafka 主题上的每条消息只会被 Spark Streaming 恰好处理一次。

## 8. 部署应用

我们可以使用预装在 Spark 安装中的 Spark-submit 脚本来部署我们的应用程序：

```powershell
$SPARK_HOME$binspark-submit 
  --class pipeline.data.cn.tuyucheng.taketoday.WordCountingAppWithCheckpoint 
  --master local[2] 
  targetspark-streaming-app-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

请注意，我们使用 Maven 创建的 jar 应该包含未标记为在范围内提供的依赖项。

一旦我们提交了这个应用程序并在我们之前创建的 Kafka 主题中发布了一些消息，我们应该会看到在我们之前创建的 Cassandra 表中发布的累积字数。

## 9.总结

总而言之，在本教程中，我们学习了如何使用 Kafka、Spark Streaming 和 Cassandra 创建一个简单的数据管道。我们还学习了如何利用 Spark Streaming 中的检查点来维护批次之间的状态。