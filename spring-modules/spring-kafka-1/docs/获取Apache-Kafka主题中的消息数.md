## 1. 概述

[Apache Kafka](https://kafka.apache.org/)是一个开源的分布式事件流平台。

在本快速教程中，我们将学习获取Kafka主题中消息数量的技术。**我们将演示编程和原生命令技术**。

## 2. 编程化技术

一个Kafka主题可能有多个分区。**我们的技术应该确保我们计算了来自每个分区的消息数**。

**我们必须遍历每个分区并检查它们的最新偏移量**。为此，我们将引出一个消费者：

```java
KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
```

第二步是**从这个消费者那里获取所有的分区**：

```java
List<TopicPartition> partitions = consumer.partitionsFor(topic).stream().map(p -> new TopicPartition(topic, p.partition()))
    .collect(Collectors.toList());
```

第三步是**在每个分区的末尾偏移消费者并将结果记录在分区Map中**：

```java
consumer.assign(partitions);
consumer.seekToEnd(Collections.emptySet());
Map<TopicPartition, Long> endPartitions = partitions.stream().collect(Collectors.toMap(Function.identity(), consumer::position));
```

最后一步是**取每个分区中的最后位置并对结果求和以获得主题中的消息数**：

```java
numberOfMessages = partitions.stream().mapToLong(p -> endPartitions.get(p)).sum();
```

## 3. Kafka原生命令

如果我们想要对Kafka主题上的消息数量执行一些自动化任务，那么编程技术是很好的选择。**但是，如果仅用于分析目的，那么创建这些服务并在机器上运行它们将是一种开销**。一个直接的选择是使用原生Kafka命令。它可以快速给出结果。

### 3.1 使用GetoffsetShell命令

在执行原生命令之前，我们必须导航到计算机上Kafka的根文件夹。以下命令向我们返回在主题tuyucheng上发布的消息数：

```shell
$ bin/kafka-run-class.sh kafka.tools.GetOffsetShell   --broker-list localhost:9092   
--topic tuyucheng   | awk -F  ":" '{sum += $3} END {print "Result: "sum}'
Result: 3
```

### 3.2 使用消费者控制台

如前所述，我们将在执行任何命令之前导航到Kafka的根文件夹。以下命令返回在主题tuyucheng上发布的消息数：

```shell
$ bin/kafka-console-consumer.sh  --from-beginning  --bootstrap-server localhost:9092 
--property print.key=true --property print.value=false --property print.partition 
--topic tuyucheng --timeout-ms 5000 | tail -n 10|grep "Processed a total of"
Processed a total of 3 messages
```

## 4. 总结

在本文中，我们研究了获取Kafka主题中消息数量的技术。我们学习了一种编程技术，可以将所有分区分配给消费者并检查最新的偏移量。

我们还看到了两种原生Kafka命令技术。一个是来自Kafka工具的GetoffsetShell命令。另一个是在控制台上运行消费者并从头开始打印消息数。