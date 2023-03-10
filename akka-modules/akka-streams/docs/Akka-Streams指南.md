## 1. 概述

在本文中，我们将研究构建在Akka actor框架之上的[akka-streams](http://doc.akka.io/docs/akka/current/scala/stream/)库，它遵循[响应流宣言](http://www.reactive-streams.org/)。**Akka Streams API使我们能够轻松地从独立的步骤组成数据转换流**。

此外，所有处理都是以响应式、非阻塞和异步的方式完成的。

## 2. Maven依赖

首先，我们需要将[akka-stream](https://central.sonatype.com/artifact/com.typesafe.akka/akka-stream_2.11/2.5.32)和[akka-stream-testkit](https://central.sonatype.com/artifact/com.typesafe.akka/akka-stream-testkit_2.11/2.5.32)库添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-stream_2.11</artifactId>
    <version>2.5.2</version>
</dependency>
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-stream-testkit_2.11</artifactId>
    <version>2.5.2</version>
</dependency>
```

## 3. Akka Streams API

要使用Akka Streams，我们需要了解核心API概念：

-   **[Source](http://doc.akka.io/japi/akka/2.5.2/akka/stream/scaladsl/Source.html)：akka-stream库中处理的入口点**-我们可以从多个来源创建此类的实例；例如，如果我们想从单个字符串创建Source可以使用single()方法，或者我们可以从Iterable的元素Source
-   **[Flow](http://doc.akka.io/japi/akka/2.5.2/akka/stream/scaladsl/Flow.html)：主要的处理构建块**-每个Flow实例都有一个输入和一个输出值
-   **Materializer**：**如果我们希望我们的流程有一些副作用，比如日志记录或保存结果，我们可以使用一个**；最常见的是，我们会将NotUsed别名作为Materializer传递，以表示我们的Flow不应有任何副作用
-   **[Sink](http://doc.akka.io/japi/akka/2.5.2/akka/stream/scaladsl/Sink.html)操作：当我们构建一个Flow时，它不会被执行，直到我们在它上面注册一个[Sink](http://doc.akka.io/japi/akka/2.5.2/akka/stream/scaladsl/Sink.html)操作**-它是一个终端操作，会触发整个Flow

## 4. 在Akka Streams中创建Flow

让我们从构建一个简单示例开始，我们将在其中展示如何**创建和组合多个Flow**以处理整数流并计算流中整数对的平均移动窗口。

我们将解析一个以分号分隔的整数字符串作为输入，为示例创建我们的akka-stream源。

### 4.1 使用Flow来解析输入

首先，让我们创建一个DataImporter类，该类将采用我们稍后将用于创建我们的Flow的ActorSystem的实例：

```java
public class DataImporter {
    private ActorSystem actorSystem;

    // standard constructors, getters...
}
```

接下来，让我们创建一个parseLine方法，该方法将从我们分隔的输入字符串中生成一个整数列表。请记住，我们在这里仅使用Java Stream API进行解析：

```java
private List<Integer> parseLine(String line) {
    String[] fields = line.split(";");
    return Arrays.stream(fields)
        .map(Integer::parseInt)
        .collect(Collectors.toList());
}
```

我们的初始Flow会将parseLine应用于我们的输入，以创建一个具有输入类型String和输出类型Integer的Flow：

```java
private Flow<String, Integer, NotUsed> parseContent() {
    return Flow.of(String.class)
        .mapConcat(this::parseLine);
}
```

当我们调用parseLine()方法时，编译器知道该lambda函数的参数将是一个字符串-与我们的Flow的输入类型相同。

请注意，我们正在使用mapConcat()方法-等同于Java 8 flatMap()方法，因为我们想将parseLine()返回的整数列表展平为整数Flow，这样处理中的后续步骤就不需要处理List。

### 4.2 使用Flow执行计算

至此，我们有了解析整数的Flow。现在，我们需要**实现将所有输入元素分组成对并计算这些对的平均值的逻辑**。

现在，我们将**创建一个Integer Flow并使用grouped()方法对它们进行分组**。

接下来，我们要计算平均值。

由于我们对处理这些平均值的顺序不感兴趣，因此**我们可以使用mapAsyncUnordered()方法使用多个线程并行计算平均值**，将线程数作为参数传递给此方法。

将作为lambda传递给Flow的操作需要返回一个CompletableFuture，因为该操作将在单独的线程中异步计算：

```java
private Flow<Integer, Double, NotUsed> computeAverage() {
    return Flow.of(Integer.class)
        .grouped(2)
        .mapAsyncUnordered(8, integers ->
            CompletableFuture.supplyAsync(() -> integers.stream()
                .mapToDouble(v -> v)
                .average()
                .orElse(-1.0)));
}
```

我们正在计算8个并行线程的平均值。请注意，我们使用Java 8 Stream API计算平均值。

### 4.3 将多个Flows组合成单个Flow

Flow API是一个流式的抽象，它允许我们**组合多个Flow实例来实现我们最终的处理目标**。我们可以有细粒度的Flow，例如，一个解析JSON，另一个做一些转换，另一个正在收集一些统计数据。

这样的粒度可帮助我们创建更多可测试的代码，因为我们可以独立测试每个处理步骤。

我们在上面创建了两个可以相互独立工作的Flows。现在，我们想将它们组合在一起。

首先，我们要解析我们的输入字符串，接下来，我们要计算元素流的平均值。

我们可以使用via()方法组合Flow：

```java
Flow<String, Double, NotUsed> calculateAverage() {
    return Flow.of(String.class)
        .via(parseContent())
        .via(computeAverage());
}
```

我们创建了一个具有输入类型String的Flow和它后面的两个其他Flow。parseContent() Flow接收一个字符串输入并返回一个整数作为输出。computeAverage() Flow采用该Integer并计算返回Double作为输出类型的平均值。

## 5. 将Sink添加到Flow中

正如我们提到的，到目前为止整个Flow还没有执行，因为它是惰性的。**要开始执行Flow，我们需要定义一个Sink**。例如，Sink操作可以将数据保存到数据库中，或将结果发送到某些外部Web服务。

假设我们有一个AverageRepository类，它具有以下将结果写入数据库的save()方法：

```java
CompletionStage<Double> save(Double average) {
    return CompletableFuture.supplyAsync(() -> {
        // write to database
        return average;
    });
}
```

现在，我们要创建一个Sink操作，使用此方法来保存Flow处理的结果。要创建我们的Sink，我们首先需要**创建一个Flow，它将我们处理的结果作为输入类型**。接下来，我们要将所有结果保存到数据库中。

同样，我们不关心元素的顺序，因此我们可以**使用mapAsyncUnordered()方法并行执行save()操作**。

要从Flow创建Sink，我们需要调用toMat()并将Sink.ignore()作为第一个参数，将Keep.right()作为第二个参数，因为我们想要返回处理的状态：

```java
private Sink<Double, CompletionStage<Done>> storeAverages() {
    return Flow.of(Double.class)
        .mapAsyncUnordered(4, averageRepository::save)
        .toMat(Sink.ignore(), Keep.right());
}
```

## 6. 定义Flow的源

我们需要做的最后一件事是**从输入String创建一个Source**。我们可以使用via()方法将calculateAverage() Flow应用于此源。

然后，要将Sink添加到处理中，我们需要调用runWith()方法并传递我们刚刚创建的storeAverages() Sink：

```java
CompletionStage<Done> calculateAverageForContent(String content) {
    return Source.single(content)
        .via(calculateAverage())
        .runWith(storeAverages(), ActorMaterializer.create(actorSystem))
        .whenComplete((d, e) -> {
            if (d != null) {
                System.out.println("Import finished ");
            } else {
                e.printStackTrace();
            }
        });
}
```

请注意，当处理完成时，我们将添加whenComplete()回调，我们可以在其中根据处理的结果执行一些操作。

## 7. 测试Akka流

我们可以使用akka-stream-testkit测试我们的处理。

**测试实际处理逻辑的最佳方法是测试所有Flow逻辑并使用TestSink触发计算并对结果进行断言**。

在我们的测试中，我们正在创建我们想要测试的Flow，接下来，我们从测试输入内容创建一个Source：

```java
@Test
public void givenStreamOfIntegers_whenCalculateAverageOfPairs_thenShouldReturnProperResults() {
    // given
    Flow<String, Double, NotUsed> tested = new DataImporter(actorSystem).calculateAverage();
    String input = "1;9;11;0";

    // when
    Source<Double, NotUsed> flow = Source.single(input).via(tested);

    // then
    flow.runWith(TestSink.probe(actorSystem), ActorMaterializer.create(actorSystem))
        .request(4)
        .expectNextUnordered(5d, 5.5);
}
```

我们检查我们是否期望四个输入参数，并且两个平均值的结果可以以任何顺序到达，因为我们的处理是以异步和并行方式完成的。

## 8. 总结

在本文中，我们研究了akka-stream库。

我们定义了一个流程，结合多个Flow来计算元素的移动平均值。然后，我们定义了一个作为流处理入口点的Source和一个触发实际处理的Sink。

最后，我们使用akka-stream-testkit为我们的处理编写了一个测试。