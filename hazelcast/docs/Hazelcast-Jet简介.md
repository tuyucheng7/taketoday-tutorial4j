## 1. 简介

在本教程中，我们将了解 Hazelcast Jet。它是由 Hazelcast, Inc. 提供的分布式数据处理引擎，构建在 Hazelcast IMDG 之上。

如果想了解 Hazelcast IMDG，[这里](https://www.baeldung.com/java-hazelcast)有一篇入门文章。

## 2. 什么是 Hazelcast Jet？

Hazelcast Jet 是一种分布式数据处理引擎，将数据视为流。它可以处理存储在数据库或文件中的数据以及由 Kafka 服务器流式传输的数据。

此外，它可以通过将流划分为子集并对每个子集应用聚合来对无限数据流执行聚合函数。此概念在 Jet 术语中称为窗口化。

我们可以在机器集群中部署 Jet，然后将我们的数据处理作业提交给它。Jet 将使集群的所有成员自动处理数据。集群的每个成员都消耗一部分数据，这使得扩展到任何级别的吞吐量变得容易。

以下是 Hazelcast Jet 的典型用例：

-   实时流处理
-   快速批处理
-   以分布式方式处理Java8 流
-   微服务中的数据处理

## 3.设置

要在我们的环境中设置 Hazelcast Jet，我们只需要向我们的pom.xml添加一个 Maven 依赖项。

我们是这样做的：

```xml
<dependency>
    <groupId>com.hazelcast.jet</groupId>
    <artifactId>hazelcast-jet</artifactId>
    <version>4.2</version>
</dependency>
```

包括这个依赖项将下载一个 10 Mb 的 jar 文件，它为我们提供了构建分布式数据处理管道所需的所有基础设施。

可在[此处](https://search.maven.org/classic/#search|ga|1|hazelcast jet)找到 Hazelcast Jet 的最新版本。

## 4. 示例应用

为了更多地了解 Hazelcast Jet，我们将创建一个示例应用程序，它接受句子输入和要在这些句子中查找的单词，并返回这些句子中指定单词的计数。

### 4.1. 流水线

管道构成了 Jet 应用程序的基本结构。管道内的处理遵循以下步骤：

-   从源读取数据
-   转换数据
-   将数据写入接收器

对于我们的应用程序，管道将从分布式List读取，应用分组和聚合的转换，最后写入分布式Map。

这是我们编写管道的方式：

```java
private Pipeline createPipeLine() {
    Pipeline p = Pipeline.create();
    p.readFrom(Sources.<String>list(LIST_NAME))
      .flatMap(word -> traverseArray(word.toLowerCase().split("W+")))
      .filter(word -> !word.isEmpty())
      .groupingKey(wholeItem())
      .aggregate(counting())
      .writeTo(Sinks.map(MAP_NAME));
    return p;
}
```

从源中读取数据后，我们将遍历数据并使用正则表达式将其拆分到空间中。之后，我们过滤掉空白。

最后，我们将单词分组，聚合它们并将结果写入Map。

### 4.2. 工作

现在我们的管道已定义，我们创建一个作业来执行管道。

下面是我们如何编写一个接受参数并返回计数的countWord函数：

```java
public Long countWord(List<String> sentences, String word) {
    long count = 0;
    JetInstance jet = Jet.newJetInstance();
    try {
        List<String> textList = jet.getList(LIST_NAME);
        textList.addAll(sentences);
        Pipeline p = createPipeLine();
        jet.newJob(p).join();
        Map<String, Long> counts = jet.getMap(MAP_NAME);
        count = counts.get(word);
        } finally {
            Jet.shutdownAll();
      }
    return count;
}
```

我们首先创建一个 Jet 实例以创建我们的作业并使用管道。接下来，我们将输入列表到分布式列表，以便它在所有实例中都可用。

然后，我们使用上面构建的管道提交作业。方法newJob()返回一个由 Jet 异步启动的可执行作业。join方法等待作业完成并在作业完成但出现错误时抛出异常。

当作业完成时，结果将在分布式Map 中检索，正如我们在管道中定义的那样。因此，我们从 Jet 实例中获取Map并根据它获取单词的计数。

最后，我们关闭了 Jet 实例。在我们的执行结束后关闭它很重要，因为Jet 实例启动了它自己的线程。否则，即使在我们的方法退出后，我们的Java进程仍然存在。

这是一个单元测试，用于测试我们为 Jet 编写的代码：

```java
@Test
public void whenGivenSentencesAndWord_ThenReturnCountOfWord() {
    List<String> sentences = new ArrayList<>();
    sentences.add("The first second was alright, but the second second was tough.");
    WordCounter wordCounter = new WordCounter();
    long countSecond = wordCounter.countWord(sentences, "second");
    assertEquals(3, countSecond);
}
```

## 5.总结

在本文中，我们了解了 Hazelcast Jet。要了解有关它及其功能的更多信息，请参阅[手册](https://docs.hazelcast.com/imdg/4.2/)。