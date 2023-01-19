## 1. 概述

Apache Flink 是一个大数据处理框架，允许程序员以非常高效和可扩展的方式处理海量数据。

在本文中，我们将介绍[Apache Flink](https://ci.apache.org/projects/flink/flink-docs-release-1.2/index.html)JavaAPI中可用的一些核心 API 概念和标准数据转换 。这个 API 的流畅风格使得它很容易与 Flink 的中心构造——分布式集合一起工作。

首先，我们将看一下 Flink 的DataSet API 转换，并使用它们来实现一个字数统计程序。然后我们将简要了解 Flink 的DataStream API，它允许以实时方式处理事件流。

## 2.Maven依赖

首先，我们需要将 Maven 依赖项添加到[flink-java](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.flink" AND a%3A"flink-java")和[flink-test-utils](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.flink" AND a%3A"flink-test-utils")库：

```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-java</artifactId>
    <version>1.2.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-test-utils_2.10</artifactId>
    <version>1.2.0</version>
    <scope>test<scope>
</dependency>
```

## 3. 核心 API 概念

使用 Flink 时，我们需要了解与其 API 相关的几件事：

-   每个 Flink 程序都对分布式数据集合执行转换。提供了多种数据转换功能，包括过滤、映射、连接、分组和聚合
-   Flink 中的接收器操作触发流的执行以产生程序所需的结果，例如将结果保存到文件系统或将其打印到标准输出
-   Flink 转换是惰性的，这意味着它们在调用sink操作之前不会执行
-   Apache Flink API 支持两种操作模式——批处理和实时。如果正在处理可以批处理模式处理的有限数据源，将使用DataSet API。如果你想实时处理无限制的数据流，你需要使用DataStream API

## 4. 数据集 API 转换

Flink 程序的入口点是[ExecutionEnvironment](https://ci.apache.org/projects/flink/flink-docs-release-1.2/api/java/org/apache/flink/api/scala/ExecutionEnvironment.html)类的一个实例——它定义了程序执行的上下文。

让我们创建一个ExecutionEnvironment来开始我们的处理：

```java
ExecutionEnvironment env
  = ExecutionEnvironment.getExecutionEnvironment();
```

请注意，当在本地计算机上启动应用程序时，它将在本地 JVM 上执行处理。如果你想在一组机器上开始处理，你需要在这些机器上安装[Apache Flink并相应地配置](https://nightlies.apache.org/flink/flink-docs-release-1.14//docs/try-flink/local_installation/)ExecutionEnvironment。

### 4.1. 创建数据集

要开始执行数据转换，我们需要为我们的程序提供数据。

让我们使用ExecutionEnvironement创建DataSet类的实例：

```java
DataSet<Integer> amounts = env.fromElements(1, 29, 40, 50);
```

可以从多个来源创建数据集，例如 Apache Kafka、CSV、文件或几乎任何其他数据源。

### 4.2. 过滤和减少

一旦创建了DataSet类的实例，就可以对其应用转换。

假设要过滤掉高于某个阈值的数字，然后将它们全部相加。可以使用filter()和reduce()转换来实现此目的：

```java
int threshold = 30;
List<Integer> collect = amounts
  .filter(a -> a > threshold)
  .reduce((integer, t1) -> integer + t1)
  .collect();

assertThat(collect.get(0)).isEqualTo(90);

```

请注意，collect()方法是触发实际数据转换的接收器操作。

### 4.3. 地图

假设有一个Person对象的数据集：

```java
private static class Person {
    private int age;
    private String name;

    // standard constructors/getters/setters
}
```

接下来，让我们创建这些对象的数据集：

```java
DataSet<Person> personDataSource = env.fromCollection(
  Arrays.asList(
    new Person(23, "Tom"),
    new Person(75, "Michael")));
```

假设只想从集合的每个对象中提取年龄字段。可以使用map()转换仅获取Person类的特定字段：

```java
List<Integer> ages = personDataSource
  .map(p -> p.age)
  .collect();

assertThat(ages).hasSize(2);
assertThat(ages).contains(23, 75);
```

### 4.4. 加入

当有两个数据集时，可能希望在某个id字段上加入它们。为此，可以使用join()转换。

让我们创建一个用户的交易和地址的集合：

```java
Tuple3<Integer, String, String> address
  = new Tuple3<>(1, "5th Avenue", "London");
DataSet<Tuple3<Integer, String, String>> addresses
  = env.fromElements(address);

Tuple2<Integer, String> firstTransaction 
  = new Tuple2<>(1, "Transaction_1");
DataSet<Tuple2<Integer, String>> transactions 
  = env.fromElements(firstTransaction, new Tuple2<>(12, "Transaction_2"));

```

两个元组中的第一个字段都是Integer类型，这是一个id字段，我们要在其上连接两个数据集。

要执行实际的加入逻辑，我们需要为地址和交易实现一个[KeySelector接口：](https://ci.apache.org/projects/flink/flink-docs-release-1.2/api/java/org/apache/flink/api/java/functions/KeySelector.html)

```java
private static class IdKeySelectorTransaction 
  implements KeySelector<Tuple2<Integer, String>, Integer> {
    @Override
    public Integer getKey(Tuple2<Integer, String> value) {
        return value.f0;
    }
}

private static class IdKeySelectorAddress 
  implements KeySelector<Tuple3<Integer, String, String>, Integer> {
    @Override
    public Integer getKey(Tuple3<Integer, String, String> value) {
        return value.f0;
    }
}
```

每个选择器只返回应该执行连接的字段。

不幸的是，这里不能使用 lambda 表达式，因为 Flink 需要通用类型信息。

接下来，让我们使用这些选择器实现合并逻辑：

```java
List<Tuple2<Tuple2<Integer, String>, Tuple3<Integer, String, String>>>
  joined = transactions.join(addresses)
  .where(new IdKeySelectorTransaction())
  .equalTo(new IdKeySelectorAddress())
  .collect();

assertThat(joined).hasSize(1);
assertThat(joined).contains(new Tuple2<>(firstTransaction, address));


```

### 4.5. 种类

假设有以下Tuple2 集合：

```java
Tuple2<Integer, String> secondPerson = new Tuple2<>(4, "Tom");
Tuple2<Integer, String> thirdPerson = new Tuple2<>(5, "Scott");
Tuple2<Integer, String> fourthPerson = new Tuple2<>(200, "Michael");
Tuple2<Integer, String> firstPerson = new Tuple2<>(1, "Jack");
DataSet<Tuple2<Integer, String>> transactions = env.fromElements(
  fourthPerson, secondPerson, thirdPerson, firstPerson);


```

如果你想按元组的第一个字段对这个集合进行排序，你可以使用sortPartitions()转换：

```java
List<Tuple2<Integer, String>> sorted = transactions
  .sortPartition(new IdKeySelectorTransaction(), Order.ASCENDING)
  .collect();

assertThat(sorted)
  .containsExactly(firstPerson, secondPerson, thirdPerson, fourthPerson);
```

## 5.字数统计

字数统计问题是一种常用于展示大数据处理框架能力的问题。基本解决方案涉及计算文本输入中的单词出现次数。让我们使用 Flink 来实现这个问题的解决方案。

作为我们解决方案的第一步，我们创建了一个LineSplitter类，它将我们的输入拆分为标记(单词)，为每个标记收集一个Tuple2键值对。在这些元组中的每一个中，键是在文本中找到的单词，值是整数一 (1)。

此类实现[FlatMapFunction](https://ci.apache.org/projects/flink/flink-docs-release-1.1/api/java/org/apache/flink/api/common/functions/FlatMapFunction.html) 接口，该接口将String作为输入并生成[Tuple2](https://nightlies.apache.org/flink/flink-docs-release-1.3/api/java/org/apache/flink/api/java/tuple/Tuple2.html) <String, Integer>：

```java
public class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
        Stream.of(value.toLowerCase().split("W+"))
          .filter(t -> t.length() > 0)
          .forEach(token -> out.collect(new Tuple2<>(token, 1)));
    }
}
```

[我们在Collector](https://ci.apache.org/projects/flink/flink-docs-release-1.0/api/java/org/apache/flink/util/class-use/Collector.html)类上调用collect()方法以在处理管道中向前推送数据。

我们的下一步也是最后一步是根据第一个元素(单词)对元组进行分组，然后对第二个元素执行求和聚合以生成单词出现的计数：

```java
public static DataSet<Tuple2<String, Integer>> startWordCount(
  ExecutionEnvironment env, List<String> lines) throws Exception {
    DataSet<String> text = env.fromCollection(lines);

    return text.flatMap(new LineSplitter())
      .groupBy(0)
      .aggregate(Aggregations.SUM, 1);
}
```

我们使用三种类型的 Flink 转换：flatMap()、groupBy()和aggregate()。

让我们编写一个测试来断言字数统计实现是否按预期工作：

```java
List<String> lines = Arrays.asList(
  "This is a first sentence",
  "This is a second sentence with a one word");

DataSet<Tuple2<String, Integer>> result = WordCount.startWordCount(env, lines);

List<Tuple2<String, Integer>> collect = result.collect();
 
assertThat(collect).containsExactlyInAnyOrder(
  new Tuple2<>("a", 3), new Tuple2<>("sentence", 2), new Tuple2<>("word", 1),
  new Tuple2<>("is", 2), new Tuple2<>("this", 2), new Tuple2<>("second", 1),
  new Tuple2<>("first", 1), new Tuple2<>("with", 1), new Tuple2<>("one", 1));
```

## 6. 数据流接口

### 6.1. 创建数据流

Apache Flink 还支持通过其 DataStream API 处理事件流。如果我们要开始消费事件，我们首先需要使用StreamExecutionEnvironment类：

```java
StreamExecutionEnvironment executionEnvironment
 = StreamExecutionEnvironment.getExecutionEnvironment();
```

接下来，我们可以使用来自各种来源的executionEnvironment创建事件流。它可能是像Apache Kafka这样的消息总线，但在这个例子中，我们将简单地从几个字符串元素创建一个源：

```java
DataStream<String> dataStream = executionEnvironment.fromElements(
  "This is a first sentence", 
  "This is a second sentence with a one word");
```

我们可以像在普通的DataSet类中一样对DataStream的每个元素应用转换：

```java
SingleOutputStreamOperator<String> upperCase = text.map(String::toUpperCase);
```

要触发执行，我们需要调用一个 sink 操作，例如print()，它只会将转换结果打印到标准输出，然后调用StreamExecutionEnvironment类上的execute()方法：

```java
upperCase.print();
env.execute();
```

它将产生以下输出：

```plaintext
1> THIS IS A FIRST SENTENCE
2> THIS IS A SECOND SENTENCE WITH A ONE WORD
```

### 6.2. 事件窗口

在实时处理事件流时，有时可能需要将事件组合在一起并在这些事件的窗口上应用一些计算。

假设我们有一个事件流，其中每个事件都是一对，由事件编号和事件发送到我们系统时的时间戳组成，并且我们可以容忍乱序的事件，但前提是它们没有晚了二十多秒。

对于这个例子，让我们首先创建一个流来模拟两个相隔几分钟的事件，并定义一个时间戳提取器来指定我们的延迟阈值：

```java
SingleOutputStreamOperator<Tuple2<Integer, Long>> windowed
  = env.fromElements(
  new Tuple2<>(16, ZonedDateTime.now().plusMinutes(25).toInstant().getEpochSecond()),
  new Tuple2<>(15, ZonedDateTime.now().plusMinutes(2).toInstant().getEpochSecond()))
  .assignTimestampsAndWatermarks(
    new BoundedOutOfOrdernessTimestampExtractor
      <Tuple2<Integer, Long>>(Time.seconds(20)) {
 
        @Override
        public long extractTimestamp(Tuple2<Integer, Long> element) {
          return element.f1  1000;
        }
    });
```

接下来，让我们定义一个窗口操作，将我们的事件分组为五秒窗口，并对这些事件应用转换：

```java
SingleOutputStreamOperator<Tuple2<Integer, Long>> reduced = windowed
  .windowAll(TumblingEventTimeWindows.of(Time.seconds(5)))
  .maxBy(0, true);
reduced.print();
```

它将获取每五秒窗口的最后一个元素，因此它会打印出：

```java
1> (15,1491221519)
```

请注意，我们看不到第二个事件，因为它到达的时间晚于指定的延迟阈值。

## 七. 总结

在本文中，我们介绍了 Apache Flink 框架并查看了其 API 提供的一些转换。

我们使用 Flink 流畅且功能强大的 DataSet API 实现了一个字数统计程序。然后我们研究了 DataStream API 并在事件流上实现了一个简单的实时转换。