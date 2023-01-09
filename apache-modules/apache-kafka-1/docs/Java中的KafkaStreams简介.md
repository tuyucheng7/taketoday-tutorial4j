## 1. 概述

在本文中，我们将研究[KafkaStreams库](https://kafka.apache.org/documentation/streams/)。

KafkaStreams由 Apache Kafka 的创建者设计。该软件的主要目标是允许程序员创建可以作为微服务工作的高效、实时、流式应用程序。

KafkaStreams使我们能够从 Kafka 主题中消费、分析或转换数据，并有可能将其发送到另一个 Kafka 主题。

为了演示KafkaStreams，我们将创建一个简单的应用程序，它从主题中读取句子，计算单词的出现次数并打印每个单词的计数。

需要注意的重要一点是，KafkaStreams库不是反应式的，也不支持异步操作和背压处理。

## 2.Maven依赖

要开始使用KafkaStreams编写流处理逻辑，我们需要添加对[kafka-streams](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.kafka" AND a%3A"kafka-streams")和[kafka-clients](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.kafka" AND a%3A"kafka-clients")的依赖：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>2.8.0</version>
</dependency>
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.8.0</version>
</dependency>

```

我们还需要安装并启动 Apache Kafka，因为我们将使用 Kafka 主题。该主题将成为我们的流媒体作业的数据源。

[我们可以从官网](https://www.confluent.io/download/)下载Kafka和其他需要的依赖。

## 3.配置KafkaStreams输入

我们要做的第一件事是输入 Kafka 主题的定义。

我们可以使用我们下载的Confluent工具——它包含一个 Kafka 服务器。它还包含我们可以用来向 Kafka 发布消息的kafka-console-producer 。

首先让我们运行我们的 Kafka 集群：

```shell
./confluent start
```

Kafka 启动后，我们可以使用APPLICATION_ID_CONFIG定义我们的数据源和应用程序名称：

```java
String inputTopic = "inputTopic";
Properties streamsConfiguration = new Properties();
streamsConfiguration.put(
  StreamsConfig.APPLICATION_ID_CONFIG, 
  "wordcount-live-test");
```

一个关键的配置参数是BOOTSTRAP_SERVER_CONFIG。这是我们刚刚启动的本地 Kafka 实例的 URL：

```java
private String bootstrapServers = "localhost:9092";
streamsConfiguration.put(
  StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, 
  bootstrapServers);
```

接下来，我们需要传递将从inputTopic 消费的消息的键和值的类型：

```java
streamsConfiguration.put(
  StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 
  Serdes.String().getClass().getName());
streamsConfiguration.put(
  StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, 
  Serdes.String().getClass().getName());
```

流处理通常是有状态的。当我们要保存中间结果时，我们需要指定STATE_DIR_CONFIG参数。

在我们的测试中，我们使用本地文件系统：

```java
this.stateDirectory = Files.createTempDirectory("kafka-streams");
streamsConfiguration.put(
  StreamsConfig.STATE_DIR_CONFIG, this.stateDirectory.toAbsolutePath().toString());

```

## 4.构建流式拓扑

一旦我们定义了我们的输入主题，我们就可以创建一个 Streaming Topology——这是对事件应该如何处理和转换的定义。

在我们的例子中，我们想实现一个单词计数器。对于发送到inputTopic 的每个句子，我们希望将其拆分为单词并计算每个单词的出现次数。

我们可以使用KStreamsBuilder类的一个实例来开始构建我们的拓扑：

```java
StreamsBuilder builder = new StreamsBuilder();
KStream<String, String> textLines = builder.stream(inputTopic);
Pattern pattern = Pattern.compile("W+", Pattern.UNICODE_CHARACTER_CLASS);

KTable<String, Long> wordCounts = textLines
  .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
  .groupBy((key, word) -> word)
  .count();
```

要实现字数统计，首先，我们需要使用正则表达式拆分值。

split 方法返回一个数组。我们正在使用flatMapValues()来展平它。否则，我们最终会得到一个数组列表，并且使用这种结构编写代码会很不方便。

最后，我们聚合每个单词的值并调用count()来计算特定单词的出现次数。

## 5. 处理结果

我们已经计算了输入消息的字数。现在让我们使用foreach()方法在标准输出上打印结果：

```java
wordCounts.toStream()
  .foreach((word, count) -> System.out.println("word: " + word + " -> " + count));
```

在生产环境中，通常这样的流式传输作业可能会将输出发布到另一个 Kafka 主题。

我们可以使用to() 方法来做到这一点：

```java
String outputTopic = "outputTopic";
wordCounts.toStream()
  .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
```

Serde类为我们提供了用于将对象序列化为字节数组的Java类型的预配置序列化程序。然后字节数组将被发送到 Kafka 主题。

我们使用String作为主题的键，使用Long作为实际计数的值。to()方法将结果数据保存到outputTopic。

## 6. 启动 KafkaStream 作业

至此，我们搭建了一个可以执行的拓扑。然而，工作还没有开始。

我们需要通过调用KafkaStreams实例上的start()方法来明确地开始我们的工作：

```java
Topology topology = builder.build();
KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);
streams.start();

Thread.sleep(30000);
streams.close();
```

请注意，我们正在等待 30 秒才能完成作业。在真实场景中，该作业将一直运行，在来自 Kafka 的事件到达时对其进行处理。

我们可以通过向我们的 Kafka 主题发布一些事件来测试我们的工作。

让我们启动一个kafka-console-producer并手动将一些事件发送到我们的inputTopic：

```shell
./kafka-console-producer --topic inputTopic --broker-list localhost:9092
>"this is a pony"
>"this is a horse and pony"

```

这样，我们向 Kafka 发布了两个事件。我们的应用程序将使用这些事件并打印以下输出：

```shell
word:  -> 1
word: this -> 1
word: is -> 1
word: a -> 1
word: pony -> 1
word:  -> 2
word: this -> 2
word: is -> 2
word: a -> 2
word: horse -> 1
word: and -> 1
word: pony -> 2
```

我们可以看到，当第一条消息到达时，pony这个词只出现了一次。但是当我们发送第二条消息时，第二次打印 pony 这个词：“ word : pony -> 2″。

## 六. 总结

本文讨论如何使用 Apache Kafka 作为数据源并使用KafkaStreams库作为流处理库来创建主流流处理应用程序。