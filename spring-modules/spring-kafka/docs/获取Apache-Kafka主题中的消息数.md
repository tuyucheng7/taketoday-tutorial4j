## 1. 概述

[Apache Kafka](https://kafka.apache.org/)是一个开源的分布式事件流平台。

在本快速教程中，我们将学习获取 Kafka 主题中消息数量的技术。我们将演示编程和本机命令技术。

## 2.程序化技术

一个 Kafka 主题可能有多个分区。我们的技术应该确保我们计算了来自每个分区的消息数。

我们必须遍历每个分区并检查它们的最新偏移量。为此，我们将介绍一个消费者：

```javascript
KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
```

第二步是从这个消费者那里得到所有的分区：

```vhdl
List<TopicPartition> partitions = consumer.partitionsFor(topic).stream().map(p -> new TopicPartition(topic, p.partition()))
    .collect(Collectors.toList());
```

第三步是在每个分区的末尾偏移消费者并将结果记录在分区映射中：

```javascript
consumer.assign(partitions);
consumer.seekToEnd(Collections.emptySet());
Map<TopicPartition, Long> endPartitions = partitions.stream().collect(Collectors.toMap(Function.identity(), consumer::position));
```

最后一步是取每个分区中的最后位置并对结果求和以获得主题中的消息数：

```ini
numberOfMessages = partitions.stream().mapToLong(p -> endPartitions.get(p)).sum();
```

## 3. Kafka Native 命令

如果我们想要对 Kafka 主题上的消息数量执行一些自动化任务，那么编程技术是很好的选择。但是，如果仅用于分析目的，那么创建这些服务并在机器上运行它们将是一种开销。一个直接的选择是使用本机 Kafka 命令。它会给出快速的结果。

### 3.1. 使用GetoffsetShell命令

在执行本机命令之前，我们必须导航到机器上 Kafka 的根文件夹。以下命令向我们返回在主题baeldung上发布的消息数：

```swift
$ bin/kafka-run-class.sh kafka.tools.GetOffsetShell   --broker-list localhost:9092   
--topic baeldung   | awk -F  ":" '{sum += $3} END {print "Result: "sum}'
Result: 3
```

### 3.2. 使用消费者控制台

如前所述，我们将在执行任何命令之前导航到 Kafka 的根文件夹。以下命令返回在主题baeldung上发布的消息数：

```python
$ bin/kafka-console-consumer.sh  --from-beginning  --bootstrap-server localhost:9092 
--property print.key=true --property print.value=false --property print.partition 
--topic baeldung --timeout-ms 5000 | tail -n 10|grep "Processed a total of"
Processed a total of 3 messages
```

## 4. 总结

在本文中，我们研究了获取 Kafka 主题中消息数量的技术。我们学习了一种编程技术，可以将所有分区分配给消费者并检查最新的偏移量。

我们还看到了两种本地 Kafka 命令技术。一个是来自 Kafka 工具的GetoffsetShell命令。另一个是在控制台上运行消费者并从头开始打印消息数。