## 1. 概述

在本教程中，我们将介绍 Apache Beam 并探索其基本概念。

我们将从演示使用 Apache Beam 的用例和好处开始，然后我们将介绍基础概念和术语。之后，我们将通过一个简单示例来说明 Apache Beam 的所有重要方面。

## 2. 什么是 Apache Beam？

Apache Beam (Batch + strEAM) 是用于批处理和流数据处理作业的统一编程模型。它提供了一个软件开发工具包来定义和构建数据处理管道以及执行它们的运行器。

Apache Beam 旨在提供一个可移植的编程层。实际上，Beam Pipeline Runners 将数据处理管道转换为与用户选择的后端兼容的 API。目前，支持这些分布式处理后端：

-   阿帕奇顶点
-   阿帕奇闪存
-   Apache Gearpump(孵化)
-   阿帕奇萨姆扎
-   阿帕奇星火
-   谷歌云数据流
-   淡褐色射流

## 3. 为什么选择 Apache Beam？

Apache Beam 融合了批处理和流数据处理，而其他人通常通过单独的 API 来处理。因此，很容易将流式流程更改为批处理流程，反之亦然，例如，当需求发生变化时。

Apache Beam 提高了可移植性和灵活性。我们专注于我们的逻辑而不是底层细节。此外，我们可以随时更改数据处理后端。

Apache Beam 有 Java、Python、Go 和 Scala SDK。事实上，团队中的每个人都可以使用他们选择的语言来使用它。

## 4. 基本概念

使用 Apache Beam，我们可以构建工作流图(管道)并执行它们。编程模型中的关键概念是：

-   PCollection – 表示一个数据集，可以是固定的批次或数据流
-   PTransform – 一种数据处理操作，它接受一个或多个PCollection并输出零个或多个PCollection
-   Pipeline – 表示PCollection 和PTransform的有向无环图，因此封装了整个数据处理作业
-   PipelineRunner——在指定的分布式处理后端执行一个Pipeline

简单来说，一个PipelineRunner执行一个Pipeline， 而一个Pipeline由PCollection 和PTransform组成。

## 5. 字数统计示例

现在我们已经了解了 Apache Beam 的基本概念，让我们设计和测试一个字数统计任务。

### 5.1. 构建 Beam 流水线

设计工作流图是每个 Apache Beam 作业的第一步。让我们定义一个字数统计任务的步骤：

1.  从来源阅读文本。
2.  将文本拆分为单词列表。
3.  小写所有单词。
4.  修剪标点符号。
5.  过滤停用词。
6.  计算每个独特的单词。

为此，我们需要使用PCollection和PTransform抽象将上述步骤转换为单个流水线。

### 5.2. 依赖关系

在我们可以实现我们的工作流图之前，我们应该将[Apache Beam 的核心依赖](https://search.maven.org/artifact/org.apache.beam/beam-sdks-java-core)项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.apache.beam</groupId>
    <artifactId>beam-sdks-java-core</artifactId>
    <version>${beam.version}</version>
</dependency>
```

Beam Pipeline Runners 依靠分布式处理后端来执行任务。让我们将[DirectRunner](https://search.maven.org/artifact/org.apache.beam/beam-runners-direct-java)添加为运行时依赖项：

```xml
<dependency>
    <groupId>org.apache.beam</groupId>
    <artifactId>beam-runners-direct-java</artifactId>
    <version>${beam.version}</version>
    <scope>runtime</scope>
</dependency>
```

与其他 Pipeline Runner 不同，DirectRunner不需要任何额外的设置，这使其成为初学者的不错选择。

### 5.3. 执行

Apache Beam 使用 Map-Reduce 编程范例(与[Java Streams](https://www.baeldung.com/java-8-streams-introduction)相同)。事实上，在我们继续之前了解[reduce()](https://www.baeldung.com/java-stream-reduce)、[filter()](https://www.baeldung.com/java-stream-filter-lambda)、[count()](https://www.baeldung.com/java-stream-filter-count)、[map()和flatMap()的基本概念是个好主意。](https://www.baeldung.com/java-difference-map-and-flatmap)

创建管道是我们要做的第一件事：

```java
PipelineOptions options = PipelineOptionsFactory.create();
Pipeline p = Pipeline.create(options);
```

现在我们应用我们的六步字数统计任务：

```java
PCollection<KV<String, Long>> wordCount = p
    .apply("(1) Read all lines", 
      TextIO.read().from(inputFilePath))
    .apply("(2) Flatmap to a list of words", 
      FlatMapElements.into(TypeDescriptors.strings())
      .via(line -> Arrays.asList(line.split("s"))))
    .apply("(3) Lowercase all", 
      MapElements.into(TypeDescriptors.strings())
      .via(word -> word.toLowerCase()))
    .apply("(4) Trim punctuations", 
      MapElements.into(TypeDescriptors.strings())
      .via(word -> trim(word)))
    .apply("(5) Filter stopwords", 
      Filter.by(word -> !isStopWord(word)))
    .apply("(6) Count words", 
      Count.perElement());
```

apply()的第一个(可选)参数是一个字符串，它只是为了更好地提高代码的可读性。以下是上面代码中每个apply()的作用：

1.  首先，我们使用TextIO 逐行读取输入文本文件。
2.  用空格分割每一行，我们将它平面映射到一个单词列表。
3.  字数不区分大小写，因此我们将所有单词小写。
4.  早些时候，我们用空格分割行，以“word!”这样的词结尾。和“词？”，所以我们删除标点符号。
5.  几乎每个英文文本中都经常出现诸如“is”和“by”之类的停用词，因此我们将其删除。
6.  最后，我们使用内置函数Count.perElement()计算唯一单词数。

如前所述，管道是在分布式后端上处理的。不可能遍历内存中的PCollection，因为它分布在多个后端。相反，我们将结果写入外部数据库或文件。

首先，我们将PCollection转换为String。然后，我们使用TextIO写入输出：

```java
wordCount.apply(MapElements.into(TypeDescriptors.strings())
    .via(count -> count.getKey() + " --> " + count.getValue()))
    .apply(TextIO.write().to(outputFilePath));
```

现在我们的Pipeline定义已经完成，我们可以运行和测试它。

### 5.4. 运行和测试

到目前为止，我们已经为字数统计任务定义了一个Pipeline 。在这一点上，让我们运行管道：

```java
p.run().waitUntilFinish();
```

在这行代码中，Apache Beam 会将我们的任务发送到多个DirectRunner实例。因此，最后将生成几个输出文件。它们将包含以下内容：

```plaintext
...
apache --> 3
beam --> 5
rocks --> 2
...
```

在 Apache Beam 中定义和运行分布式作业就这么简单且富有表现力。[为了进行比较， Apache Spark](https://www.baeldung.com/apache-spark)、[Apache Flink](https://www.baeldung.com/apache-flink)和[Hazelcast Jet](https://www.baeldung.com/hazelcast-jet)上也提供了字数统计实现。

## 6. 我们何去何从？

我们成功地计算了输入文件中的每个单词，但我们还没有最常用单词的报告。当然，对PCollection进行排序是我们下一步要解决的好问题。

稍后，我们可以了解更多关于 Windowing、Triggers、Metrics 和更复杂的 Transforms。[Apache Beam 文档](https://beam.apache.org/documentation/)提供了深入的信息和参考资料。

## 七. 总结

在本教程中，我们了解了 Apache Beam 是什么以及为什么它优于替代方案。我们还通过字数统计示例演示了 Apache Beam 的基本概念。