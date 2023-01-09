## 1. 概述

在本教程中，我们将了解Kafka 如何通过新引入的 Transactional API 确保生产者和消费者应用程序之间的 exactly-once 交付。

此外，我们将使用此 API 来实现事务性生产者和消费者，以在 WordCount 示例中实现端到端的一次性交付。

## 2. Kafka中的消息传递

由于各种故障，消息系统无法保证生产者和消费者应用程序之间的消息传递。根据客户端应用程序与此类系统交互的方式，可以使用以下消息语义：

-   如果一个消息传递系统永远不会一条消息，但可能会错过偶尔的消息，我们称之为至多一次
-   或者，如果它永远不会错过一条消息但可能会重复偶尔出现的消息，我们称它为至少一次
-   但是，如果它总是不重复地传递所有消息，那就是恰好一次

最初，Kafka 只支持最多一次和至少一次消息传递。

然而，在 Kafka broker 和客户端应用程序之间引入 Transactions 确保了 Kafka 中的 exactly-once delivery。为了更好地理解它，让我们快速回顾一下事务性客户端 API。

## 3.Maven依赖

要使用事务 API，我们需要在 pom 中安装[Kafka 的Java客户端：](https://search.maven.org/search?q=g:org.apache.kafka AND a:kafka-clients)

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.8.0</version>
</dependency>
```

## 4. 交易消费-转化-生产循环

对于我们的示例，我们将使用来自输入主题sentences的消息。

然后对于每个句子，我们将计算每个单词并将单个单词计数发送到输出主题 counts。

在示例中，我们假设句子主题中已经有可用的交易数据。

### 4.1. 事务感知生产者

因此，让我们首先添加一个典型的 Kafka 生产者。

```java
Properties producerProps = new Properties();
producerProps.put("bootstrap.servers", "localhost:9092");
```

此外，我们还需要指定一个 transactional.id 并启用 幂等性：

```java
producerProps.put("enable.idempotence", "true");
producerProps.put("transactional.id", "prod-1");

KafkaProducer<String, String> producer = new KafkaProducer(producerProps);
```

因为我们启用了幂等性，Kafka 将使用此事务 ID 作为其算法的一部分来删除此生产者 发送的任何消息，确保幂等性。

简单地说，如果生产者不小心多次向 Kafka 发送相同的消息，这些设置可以让它注意到。

我们需要做的就是 确保每个生产者的交易 ID 都是不同的，尽管在重新启动时是一致的。

### 4.2. 为交易启用生产者

准备就绪后，我们还需要调用 initTransaction 来准备生产者使用事务：

```java
producer.initTransactions();
```

这会将生产者注册为可以使用交易的经纪人，通过其 transactional.id 和序列号或 epoch对其进行标识。反过来，代理将使用这些将任何操作提前写入事务日志。

因此，代理将从该日志中删除属于具有相同事务 ID 和更早 纪元 的生产者的任何操作，并假定它们来自已失效的事务。

### 4.3. 交易感知型消费者

我们消费的时候，可以依次读取一个topic分区上的所有消息。但是，我们可以使用 isolation.level指示我们应该等待读取事务消息，直到关联的事务已提交：

```java
Properties consumerProps = new Properties();
consumerProps.put("bootstrap.servers", "localhost:9092");
consumerProps.put("group.id", "my-group-id");
consumerProps.put("enable.auto.commit", "false");
consumerProps.put("isolation.level", "read_committed");
KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
consumer.subscribe(singleton(“sentences”));
```

使用read_committed值可确保我们在事务完成之前不会读取任何事务性消息。

isolation.level的 默认值为 read_uncommitted。

### 4.4. 按事务消费和转换

现在我们已经将生产者和消费者都配置为以事务方式写入和读取，我们可以使用输入主题中的记录并计算每条记录中的每个单词：

```java
ConsumerRecords<String, String> records = consumer.poll(ofSeconds(60));
Map<String, Integer> wordCountMap =
  records.records(new TopicPartition("input", 0))
    .stream()
    .flatMap(record -> Stream.of(record.value().split(" ")))
    .map(word -> Tuple.of(word, 1))
    .collect(Collectors.toMap(tuple -> 
      tuple.getKey(), t1 -> t1.getValue(), (v1, v2) -> v1 + v2));
```

请注意，上面的代码没有任何事务性。但是，由于我们使用了 read_committed，这意味着在同一事务中写入输入主题的消息在全部写入之前不会被该消费者读取。

现在，我们可以将计算出的字数发送到输出主题。

让我们看看我们如何产生我们的结果，也是事务性的。

### 4.5. 发送接口

要将我们的计数作为新消息发送，但在同一事务中，我们调用beginTransaction：

```java
producer.beginTransaction();
```

然后，我们可以将每一个写到我们的“计数”主题中，键是单词，计数是值：

```java
wordCountMap.forEach((key,value) -> 
    producer.send(new ProducerRecord<String,String>("counts",key,value.toString())));
```

请注意，因为生产者可以按键对数据进行分区，这意味着 事务性消息可以跨越多个分区，每个分区都由不同的消费者读取。 因此，Kafka broker 将存储一个事务的所有更新分区的列表。

另请注意，在事务中，生产者可以使用多个线程并行发送记录。

### 4.6. 承诺抵消

最后，我们需要提交刚刚消费完的偏移量。对于事务，我们像往常一样将偏移量提交回我们从中读取它们的输入主题。 同样， 我们 将它们发送到生产者的交易。

我们可以在一次调用中完成所有这些，但我们首先需要计算每个主题分区的偏移量：

```java
Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
for (TopicPartition partition : records.partitions()) {
    List<ConsumerRecord<String, String>> partitionedRecords = records.records(partition);
    long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();
    offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
}
```

请注意，我们向事务提交的是即将到来的偏移量，这意味着我们需要加 1。

然后我们可以将计算出的偏移量发送到交易：

```java
producer.sendOffsetsToTransaction(offsetsToCommit, "my-group-id");
```

### 4.7. 提交或中止交易

最后，我们可以提交事务，这将自动将偏移量写入 consumer_offsets 主题以及事务本身：

```java
producer.commitTransaction();
```

这会将任何缓冲的消息刷新到相应的分区。此外，Kafka 代理使该事务中的所有消息都可供消费者使用。

当然，如果我们在处理过程中出现任何问题，比如捕获异常，我们可以调用 abortTransaction：

```java
try {
  // ... read from input topic
  // ... transform
  // ... write to output topic
  producer.commitTransaction();
} catch ( Exception e ) {
  producer.abortTransaction();
}
```

并删除所有缓冲的消息并从代理中删除事务。

如果我们在代理配置的max.transaction.timeout.ms之前既不提交也不中止 ， Kafka 代理将中止事务本身。 此属性的默认值为 900,000 毫秒或 15 分钟。

## 5. 其他 消费-转化-生产循环

我们刚刚看到的是一个基本的消费-转换-生产循环，它读取和写入同一个 Kafka 集群。

相反，必须读写不同 Kafka 集群的应用程序必须使用较旧的 commitSync 和 commitAsync API。通常，应用程序会将消费者偏移量存储到其外部状态存储中以保持事务性。

## 六. 总结

对于数据关键型应用程序，端到端的一次性处理通常是必不可少的。

在本教程中，我们看到了如何使用 Kafka 通过事务来做到这一点，并且我们实现了一个基于事务的字数统计示例来说明原理。