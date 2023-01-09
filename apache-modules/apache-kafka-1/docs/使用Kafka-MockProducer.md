## 1. 概述

Kafka 是一个围绕分布式消息队列构建的消息处理系统。它提供了一个Java库，以便应用程序可以向 Kafka 主题写入数据或从中读取数据。

现在，由于大部分业务领域逻辑都是通过单元测试来验证的，因此应用程序通常会模拟JUnit 中的所有 I/O 操作。Kafka 还提供了一个MockProducer 来模拟生产者应用程序。

在本教程中，我们将首先实现一个 Kafka 生产者应用程序。稍后，我们将实施单元测试以验证使用MockProducer的常见生产者操作。

## 2.Maven依赖

在我们实现生产者应用程序之前，我们将为[kafka-clients](https://search.maven.org/artifact/org.apache.kafka/kafka-clients)添加一个 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.5.0</version>
</dependency>
```

## 3. 模拟制作人

kafka-clients 库包含一个用于在 Kafka 中发布和使用消息的Java库。生产者应用程序可以使用这些 API 将键值记录发送到 Kafka 主题：

```java
public class KafkaProducer {

    private final Producer<String, String> producer;

    public KafkaProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    public Future<RecordMetadata> send(String key, String value) {
        ProducerRecord record = new ProducerRecord("topic_sports_news", key, value);
        return producer.send(record);
    }
}
```

任何 Kafka 生产者都必须在客户端的库中实现 Producer 接口。Kafka 还提供了一个KafkaProducer类，它是对 Kafka broker 执行 I/O 操作的具体实现。

此外，Kafka 提供了一个 MockProducer ，它实现了相同的 Producer 接口，并模拟了KafkaProducer中实现的所有 I/O 操作：

```java
@Test
void givenKeyValue_whenSend_thenVerifyHistory() {

    MockProducer mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());

    kafkaProducer = new KafkaProducer(mockProducer);
    Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send("soccer", 
      "{"site" : "baeldung"}");

    assertTrue(mockProducer.history().size() == 1);
}
```

尽管也可以使用[Mockito](https://www.baeldung.com/mockito-series)模拟此类 I/O 操作，但 MockProducer使我们能够访问许多我们需要在模拟之上实现的功能。其中一个特性是history() 方法。 MockProducer 缓存 调用send() 的记录，从而允许我们验证生产者的发布行为。

此外，我们还可以验证主题名称、分区、记录键或值等元数据：

```java
assertTrue(mockProducer.history().get(0).key().equalsIgnoreCase("data"));
assertTrue(recordMetadataFuture.get().partition() == 0);
```

## 4. 模拟 Kafka 集群

到目前为止，在我们的模拟测试中，我们假设一个主题只有一个分区。然而，为了实现生产者线程和消费者线程之间的最大并发性，Kafka 主题通常被拆分为多个分区。

这允许生产者将数据写入多个分区。这通常是通过根据键对记录进行分区并将特定键映射到特定分区来实现的：

```java
public class EvenOddPartitioner extends DefaultPartitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, 
      byte[] valueBytes, Cluster cluster) {
        if (((String)key).length() % 2 == 0) {
            return 0;
        }
        return 1;
    }
}
```

因此，所有偶数长度的密钥将发布到分区“0”，同样，奇数长度的密钥将发布到分区“1”。

MockProducer 使我们能够通过模拟具有多个分区的 Kafka 集群来验证此类分区分配算法：

```java
@Test
void givenKeyValue_whenSendWithPartitioning_thenVerifyPartitionNumber() 
  throws ExecutionException, InterruptedException {
    PartitionInfo partitionInfo0 = new PartitionInfo(TOPIC_NAME, 0, null, null, null);
    PartitionInfo partitionInfo1 = new PartitionInfo(TOPIC_NAME, 1, null, null, null);
    List<PartitionInfo> list = new ArrayList<>();
    list.add(partitionInfo0);
    list.add(partitionInfo1);

    Cluster cluster = new Cluster("kafkab", new ArrayList<Node>(), list, emptySet(), emptySet());
    this.mockProducer = new MockProducer<>(cluster, true, new EvenOddPartitioner(), 
      new StringSerializer(), new StringSerializer());

    kafkaProducer = new KafkaProducer(mockProducer);
    Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send("partition", 
      "{"site" : "baeldung"}");

    assertTrue(recordMetadataFuture.get().partition() == 1);
}
```

我们模拟了一个具有两个分区 0 和 1 的集群 。然后我们可以验证EvenOddPartitioner 是否将记录发布到分区 1。

## 5. MockProducer模拟错误

到目前为止，我们只是模拟了生产者成功将记录发送到 Kafka 主题。但是，如果写入记录时出现异常怎么办？

应用程序通常通过重试或将异常抛给客户端来处理此类异常。

MockProducer允许我们在send()期间模拟异常，以便我们可以验证异常处理代码：

```java
@Test
void givenKeyValue_whenSend_thenReturnException() {
    MockProducer<String, String> mockProducer = new MockProducer<>(false, 
      new StringSerializer(), new StringSerializer())

    kafkaProducer = new KafkaProducer(mockProducer);
    Future<RecordMetadata> record = kafkaProducer.send("site", "{"site" : "baeldung"}");
    RuntimeException e = new RuntimeException();
    mockProducer.errorNext(e);

    try {
        record.get();
    } catch (ExecutionException | InterruptedException ex) {
        assertEquals(e, ex.getCause());
    }
    assertTrue(record.isDone());
}
```

这段代码中有两点值得注意。

首先，我们调用 MockProducer 构造函数， autoComplete 为false。这告诉MockProducer在完成send()方法之前等待输入。

其次，我们将调用mockProducer.errorNext(e)，以便 MockProducer为最后一次send()调用返回异常 。

## 6. 使用 MockProducer 模拟事务写入

Kafka 0.11 引入了 Kafka 经纪人、生产者和消费者之间的交易。这允许在 Kafka 中使用端到端的[Exactly-Once 消息传递语义。](https://www.baeldung.com/kafka-exactly-once)简而言之，这意味着事务生产者只能将记录发布到具有[两阶段提交](https://www.baeldung.com/transactions-intro)协议的代理。

MockProducer 还支持事务写入并允许我们验证此行为：

```java
@Test
void givenKeyValue_whenSendWithTxn_thenSendOnlyOnTxnCommit() {
    MockProducer<String, String> mockProducer = new MockProducer<>(true, 
      new StringSerializer(), new StringSerializer())

    kafkaProducer = new KafkaProducer(mockProducer);
    kafkaProducer.initTransaction();
    kafkaProducer.beginTransaction();
    Future<RecordMetadata> record = kafkaProducer.send("data", "{"site" : "baeldung"}");

    assertTrue(mockProducer.history().isEmpty());
    kafkaProducer.commitTransaction();
    assertTrue(mockProducer.history().size() == 1);
}
```

由于MockProducer 也支持与具体KafkaProducer相同的 API ，因此它只会在我们提交事务后 更新 历史记录 。这种模拟行为可以帮助应用程序验证是否为每个事务调用了commitTransaction() 。

## 七. 总结

在本文中，我们查看 了kafka-client 库的MockProducer 类 。我们讨论了 MockProducer 实现与具体KafkaProducer相同的层次结构 ，因此，我们可以使用 Kafka 代理模拟所有 I/O 操作。

我们还讨论了一些复杂的模拟场景，并能够使用MockProducer 测试异常、分区和事务。