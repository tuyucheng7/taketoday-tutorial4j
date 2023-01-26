## 一、概述

在本教程中，我们将探讨如何在 Java中完成*流*数据的批处理。**我们将看到使用本机 Java 功能和一些第三方库的示例。**

## *2.流*数据的批处理是什么意思？

***Java中流\*数据的批处理是指将一个大的数据集划分为更小的、更易于管理的块并按顺序处理它们的做法**。在这个场景中，处理的数据源来自一个数据流。

出于多种原因，这可能是有利的，包括提高数据处理效率、处理可能无法一次放入内存中的非常庞大的数据集，以及提供一种使用多个处理器并行处理数据的机制。

但是，在实施批处理时可能会出现各种问题：

-   设置可接受的批大小：如果批大小太小，处理每个批的开销可能会变得很大。但是，如果批次大小太大，处理每个批次的时间可能会太长，这会导致流处理管道出现延迟。
-   状态管理：为了跟踪中间结果或保证每个批次的处理与之前的批次一致，在采用批处理时通常需要保留批次之间的状态。使用分散系统的复杂性增加了状态管理的难度。
-   容错性：在批量处理大型数据集时，确保在发生故障时可以继续处理是至关重要的。这可能很困难，因为可能需要存储大量的中间状态才能恢复处理。

*在本文中，为了简单明了，我们将只关注 Java 中Stream*数据的批处理，而不是如何解决上述问题。

## 3. 使用 Java Streams API 进行批处理

首先，我们必须注意我们将使用的一些关键概念。首先，我们有[Streams API](https://www.baeldung.com/java-8-streams-introduction)，这是 Java 8 中引入的一个主要特性。在 Streams API 中，我们将使用[*Stream*](https://www.baeldung.com/java-8-streams-introduction#1-stream-creation)类。

在这种情况下，我们需要考虑声明的数据流只能被调用一次。如果我们尝试对同一个数据流进行第二次操作，我们会得到一个*IllegalStateException*。一个简单的例子向我们展示了这种行为：

```java
Stream<String> coursesStream = Stream.of("Java", "Frontend", "Backend", "Fullstack");
Stream<Integer> coursesStreamLength = coursesStream.map(String::length);
// we get java.lang.IllegalStateException
Stream<String> emphasisCourses = coursesStream.map(course -> course + "!");复制
```

其次，我们将使用函数式风格处理以下部分中的大多数示例。有些例子有副作用，我们必须以函数式编程风格尽量避免它们。

在我们构建我们的代码示例之前，让我们定义我们的测试数据流、批量大小和预期的批量结果。

我们的数据流将是一个整*数值*流*：*

```java
Stream<Integer> data = IntStream.range(0, 34).boxed();复制
```

然后，我们的批量大小为 10：

```java
private final int BATCH_SIZE = 10;复制
```

最后，让我们定义我们的预期批次：

```java
private final List<Integer> firstBatch = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
private final List<Integer> secondBatch = List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
private final List<Integer> thirdBatch = List.of(20, 21, 22, 23, 24, 25, 26, 27, 28, 29);
private final List<Integer> fourthBatch = List.of(30, 31, 32, 33);复制
```

接下来，让我们看一些例子。

## 4. 使用*迭代器*

第一种方法使用*[Iterator](https://www.baeldung.com/java-iterator)*接口的自定义实现。我们定义了一个*CustomBatchIterator*类，我们可以在初始化*Iterator*的新实例时设置批量大小。

让我们进入代码：

```java
public class CustomBatchIterator<T> implements Iterator<List<T>> {
    private final int batchSize;
    private List<T> currentBatch;
    private final Iterator<T> iterator;
    public CustomBatchIterator(Iterator<T> sourceIterator, int batchSize) {
        this.batchSize = batchSize;
        this.iterator = sourceIterator;
    }
    @Override
    public List<T> next() {
        return currentBatch;
    }
    @Override
    public boolean hasNext() {
        prepareNextBatch();
        return currentBatch != null && !currentBatch.isEmpty();
    }
    private void prepareNextBatch() {
        currentBatch = new ArrayList<>(batchSize);
        while (iterator.hasNext() && currentBatch.size() < batchSize) {
            currentBatch.add(iterator.next());
        }
    }
}复制
```

在这里，我们覆盖了*CustomBatchIterator*类中*Iterator*接口的*hasNext()*和*next()*方法。如果当前批次不为空，则*hasNext()方法通过执行**prepareNextBatch()*方法准备下一批数据。我们需要使用*next()*方法来获取最新的信息。

prepareNextBatch *()*方法首先使用源迭代器中的元素填充当前批次，直到批次完成或源迭代器用完元素，以先发生者为准。*currentBatch*被初始化为一个空列表，其大小等于*batchSize*。

下一步是向我们的类添加两个*静态*方法：

```java
public class CustomBatchIterator<T> implements Iterator<List<T>> {

    // other methods

    public static <T> Stream<List<T>> batchStreamOf(Stream<T> stream, int batchSize) {
        return stream(new CustomBatchIterator<>(stream.iterator(), batchSize));
    }
    private static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, ORDERED), false);
    }
}复制
```

我们的*batchStreamOf()*方法从数据流中生成批处理流。它通过实例化*CustomBatchIterator*类并将其传递给*stream()*方法来实现这一点，该方法从*Iterator*生成*Stream 。*

我们的*stream()*方法使用*Spliterators.spliteratorUnknownSize()方法从**Iterator*创建一个[*Spliterator*](https://www.baeldung.com/java-spliterator)（可以使用流探索的特殊迭代器） ，然后将*Spliterator*提供给*StreamSupport.stream()*方法以构建流。

现在，是时候测试我们的实现了：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingSpliterator_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>();
    CustomBatchIterator.batchStreamOf(data, BATCH_SIZE).forEach(result::add);
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

在上面的测试中，我们将数据流和批量大小传递给*batchStreamOf()*方法。然后，我们检查数据处理后是否有四批。

## 5. 使用集合 API

我们的下一个示例使用 Collection API，并且比第一个示例相对更直接。

让我们看看我们的测试用例：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingCollectionAPI_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = data.collect(Collectors.groupingBy(it -> it / BATCH_SIZE))
      .values();
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

我们在此代码片段中使用 Java Streams API 中的*Collectors.groupingBy()方法，通过使用**it -> it / BATCH_SIZE* lambda 表达式计算的键对数据流中的元素进行分组。lambda 表达式将每个元素除以*BATCH_SIZE*，结果作为键返回。

然后，我们调用地图的*值*方法来检索元素列表的集合，我们将其保存在结果变量中。

**对于大型数据集，我们可以使用\*Stream中的\*[\*parallel()\*](https://www.baeldung.com/java-when-to-use-parallel-stream)方法。但是，我们需要考虑到执行顺序是我们无法控制的。**它可能会在我们每次运行程序时发生变化。

*让我们使用parallel()*检查我们的测试用例：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchParallelUsingCollectionAPI_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = data.parallel()
      .collect(Collectors.groupingBy(it -> it / BATCH_SIZE))
      .values();
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

## 6.RxJava

[RxJava](https://www.baeldung.com/rx-java)是 ReactiveX 的 Java 版本，它是一个使用可观察序列编写异步和基于事件的程序的库。我们可以将它与 Streams API 结合使用，以在 Java 中进行批处理。

首先，让我们在*pom.xml*文件中添加它的[依赖项：](https://search.maven.org/search?q=g:io.reactivex.rxjava3 AND a:rxjava)

```xml
<dependency>
    <groupId>io.reactivex.rxjava3</groupId>
    <artifactId>rxjava</artifactId>
    <version>3.1.5</version>
</dependency>复制
```

我们的下一步是实现测试用例：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingRxJavaV3_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>();
    Observable.fromStream(data)
      .buffer(BATCH_SIZE)
      .subscribe(result::add);
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

为了将数据流划分为可管理的块，此代码使用 RxJava 库中的*buffer()*运算符，每个块的大小由变量*BATCH_SIZE*确定。

此外，我们使用*Observable.fromStream()*方法从数据流创建一个*Observable 。*我们以*BATCH_SIZE*作为输入调用*Observable*的*buffer()*方法。*Observable*项目被分类为我们选择大小的列表，每个列表作为流中的新项目发出。

结果是一个*Observable*，并在其上调用*subscribe()*方法，并以*result::add*作为参数。*这将创建对Observable*的订阅，并且每次*Observable*发出一个项目时，都会调用结果列表的*add*方法。在这种情况下，*Observable*的输出由聚合成集合的元素列表组成。

## 7. 瓦弗

[Vavr](https://www.baeldung.com/vavr)是一个函数式编程库，具有不可变集合和其他函数式数据结构。

在这种情况下，我们将其[依赖](https://search.maven.org/search?q=g:io.vavr AND a:vavr)项添加到我们的*pom.xml*文件中：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>1.0.0-alpha-4</version>
</dependency>复制
```

现在，让我们看看实际的测试用例：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingVavr_thenFourBatchesAreObtained() {
    List<List<Integer>> result = Stream.ofAll(data)
      .toList()
      .grouped(BATCH_SIZE)
      .toList();
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

*Stream.ofAll()*方法使用*Stream.ofAll* ()方法将数据集转换为流。最后，我们使用*Stream*的*toList()*方法将它变成一个*列表。*这个最终*列表*作为参数传递给*grouped()*方法以及值*BATCH_SIZE*。此方法返回一个有序*列表*，其中包含从原始列表中获取的*BATCH_SIZE元素，并在每个内部列表中复制一次。*

***List\*类来自上述测试中的**io.vavr.collection***而\*****不是来自*****java.util.List\****。*

## 8.反应堆

批处理的下一个选项是使用[Reactor](https://www.baeldung.com/reactor-core)库。除了批处理之外，Reactor 是一个用于响应式编程的 Java 库，它还提供了多个用于处理流的运算符。**在这种情况下，我们将使用[\*Flux\*](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)来进行批处理**。

对于此示例，让我们将[依赖](https://search.maven.org/search?q=g:io.projectreactor AND a:reactor-core)项添加到我们的*pom.xml*文件中：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.5.1</version>
</dependency>复制
```

让我们实现我们的测试用例：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingReactor_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>();
    Flux.fromStream(data)
      .buffer(BATCH_SIZE)
      .subscribe(result::add);
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

要从*java.util.stream.Stream*对象创建*Flux*，我们使用*Flux.fromStream()*方法。当我们想使用*Flux*类提供的响应式运算符处理流的元素时，这很方便。

*buffer()*运算符用于将元素分批放入固定大小的列表中。*Flux* 在发出新元素时被添加到当前列表中。当列表达到合适的大小时，*Flux*发出它，并形成一个新的列表。这对于批处理优化很有用，例如减少数据库查询或网络请求的数量。

最后，*subscribe()*方法添加了一个*Flux*订阅者。*订阅者接收Flux*发出的项目。接下来，它将它们添加到结果对象中。*subscribe()*方法生成一个*Subscription*对象，该对象可用于调节数据流并在不再需要时取消订阅*Flux 。*

## 9.阿帕奇公地

我们可以使用[Apache Commons Collections](https://www.baeldung.com/apache-commons-collection-utils)等强大的库来执行批处理。

让我们在*pom.xml*文件中添加它的[依赖项：](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-collections4)

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>复制
```

我们的测试实现很简单：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingApacheCommon_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>(ListUtils
      .partition(data.collect(Collectors.toList()), BATCH_SIZE));
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

*partition()*方法是 Apache Commons *ListUtils*实用程序方法，它接受一个*列表*和一个大小。它生成一个*List* of *List* s的 List *，*每个内部*List*的最大尺寸为所提供的尺寸。我们可以注意到，数据流**在传递给*****partition()\*****方法之前****被转换为一个\*列表\***。

## 10.番石榴

接下来，我们有[Guava](https://www.baeldung.com/guava-guide)库。Guava 提供了多种用于处理集合的实用方法，包括批处理。

让我们 在*pom.xml*文件中添加[依赖项：](https://search.maven.org/search?q=g:com.google.guava AND a:guava)

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>复制
```

现在，让我们看看我们的工作示例：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingGuava_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>();
    Iterators.partition(data.iterator(), BATCH_SIZE).forEachRemaining(result::add);
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

*Iterators.partition()*方法可以帮助将大型数据集分解成更小的块进行处理，例如并行分析数据或将其批量加载到数据库中。

我们使用*Iterators.partition()*方法将数据的迭代器拆分*为*一系列更小的*迭代**器*。传递给*Iterators.partition()*方法的数据是我们数据流中的*Iterator*。此外，我们将*BATCH_SIZE*传递给它。

## 11.独眼巨人

最后，我们有了基于[jool](https://www.baeldung.com/jool)库的 Cyclops库。Cyclops React 是一个包含多个用于与流交互的运算符的库，其中一些用于批处理。

让我们将它的[依赖](https://search.maven.org/search?q=g:com.oath.cyclops AND a:cyclops)添加到我们的*pom.xml*中：

```xml
<dependency>
    <groupId>com.oath.cyclops</groupId>
    <artifactId>cyclops</artifactId>
    <version>10.4.1</version>
</dependency>复制
```

让我们看看最后一个例子的代码：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingCyclops_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>();
    ReactiveSeq.fromStream(data)
      .grouped(BATCH_SIZE)
      .toList()
      .forEach(value -> result.add(value.collect(Collectors.toList())));
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

*ReactiveSeq*类是一种反应序列。此外，*ReactiveSeq.fromStream()*方法将数据流转换为反应序列。然后，数据被分组为*BATCH_SIZE*的批次。然后将处理后的数据收集到整数*List*的集合中*。*

但是，我们可以使用*LazySeq*获得惰性的函数式风格。在这种情况下，我们只需要将*ReactiveSeq*替换为*LazySeq*：

```java
@Test
public void givenAStreamOfData_whenIsProcessingInBatchUsingCyclopsLazy_thenFourBatchesAreObtained() {
    Collection<List<Integer>> result = new ArrayList<>();
    LazySeq.fromStream(data)
      .grouped(BATCH_SIZE)
      .toList()
      .forEach(value -> result.add(value.collect(Collectors.toList())));
    assertTrue(result.contains(firstBatch));
    assertTrue(result.contains(secondBatch));
    assertTrue(result.contains(thirdBatch));
    assertTrue(result.contains(fourthBatch));
}复制
```

## 12.结论

在本文中，我们学习了几种在 Java中完成*Stream*批处理的方法。我们探索了几种替代方案，从 Java 原生 API 到一些流行的库，如 RxJava、Vavr 和 Apache Commons。