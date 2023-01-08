## 1. 概述

Java 8 引入了[Stream API](https://www.baeldung.com/java-8-streams)，可以轻松地将集合作为数据流进行迭代。创建并行执行的流并利用多个处理器内核也非常容易。

我们可能认为将工作分配到更多内核上总是更快。但通常情况并非如此。

在本教程中，我们将探讨顺序流和并行流之间的区别。我们将首先查看并行流使用的默认 fork-join 池。

我们还将考虑使用并行流的性能影响，包括内存位置和拆分/合并成本。

最后，我们将建议何时将顺序流转换为并行流。

## 延伸阅读：

## [Java 8 Stream API 教程](https://www.baeldung.com/java-8-streams)

本文通过大量示例介绍了Java8 Stream API 提供的可能性和操作。

[阅读更多](https://www.baeldung.com/java-8-streams)→

## [Java 并行收集器库指南](https://www.baeldung.com/java-parallel-collectors)

了解如何使用 Parallel Collectors，这是一个JavaStream API 收集器库，旨在克服标准 Parallel Streams 的限制。

[阅读更多](https://www.baeldung.com/java-parallel-collectors)→

## [将Java流收集到不可变集合](https://www.baeldung.com/java-stream-immutable-collection)

了解如何将Java流收集到不可变集合中。

[阅读更多](https://www.baeldung.com/java-stream-immutable-collection)→

## 2.Java中的流

Java 中的[流](https://www.baeldung.com/java-8-streams-introduction)只是数据源的包装器，使我们能够以方便的方式对数据执行批量操作。

它不存储数据或对基础数据源进行任何更改。相反，它增加了对数据管道上函数式操作的支持。

### 2.1. 顺序流

默认情况下，Java 中的任何流操作都是按顺序处理的，除非明确指定为并行。

顺序流使用单个线程来处理管道：

```java
List<Integer> listOfNumbers = Arrays.asList(1, 2, 3, 4);
listOfNumbers.stream().forEach(number ->
    System.out.println(number + " " + Thread.currentThread().getName())
);
```

此顺序流的输出是可预测的。列表元素将始终按有序顺序打印：

```bash
1 main
2 main
3 main
4 main
```

### 2.2. 并行流

Java 中的任何流都可以轻松地从顺序流转换为并行流。

我们可以通过将parallel方法添加到顺序流或使用集合的parallelStream方法创建流来实现这一点：

```java
List<Integer> listOfNumbers = Arrays.asList(1, 2, 3, 4);
listOfNumbers.parallelStream().forEach(number ->
    System.out.println(number + " " + Thread.currentThread().getName())
);
```

并行流使我们能够在不同的内核上并行执行代码。最终结果是每个单独结果的组合。

但是，执行顺序是我们无法控制的。它可能会在我们每次运行程序时发生变化：

```bash
4 ForkJoinPool.commonPool-worker-3
2 ForkJoinPool.commonPool-worker-5
1 ForkJoinPool.commonPool-worker-7
3 main
```

## 3. Fork-Join 框架

并行流利用[fork-join](https://www.baeldung.com/java-fork-join)框架及其公共工作线程池。

fork-join 框架在Java7 中被添加到java.util.concurrent中以处理多线程之间的任务管理。

### 3.1. 拆分源

fork-join 框架负责在工作线程之间拆分源数据并处理任务完成时的回调。

让我们看一个并行计算整数和的例子。

我们将使用[reduce](https://www.baeldung.com/java-stream-reduce)方法并将 5 添加到起始总和，而不是从零开始：

```java
List<Integer> listOfNumbers = Arrays.asList(1, 2, 3, 4);
int sum = listOfNumbers.parallelStream().reduce(5, Integer::sum);
assertThat(sum).isNotEqualTo(15);
```

在顺序流中，此操作的结果将为 15。

但由于reduce操作是并行处理的，所以数字 5 实际上会在每个工作线程中相加：

[![java流reduce2](https://www.baeldung.com/wp-content/uploads/2021/05/java_stream_reduce2.png)](https://www.baeldung.com/wp-content/uploads/2021/05/java_stream_reduce2.png)

实际结果可能会有所不同，具体取决于公共 fork-join 池中使用的线程数。

为了解决这个问题，应该在并行流之外添加数字 5：

```java
List<Integer> listOfNumbers = Arrays.asList(1, 2, 3, 4);
int sum = listOfNumbers.parallelStream().reduce(0, Integer::sum) + 5;
assertThat(sum).isEqualTo(15);
```

因此，我们需要注意哪些操作可以并行运行。

### 3.2. 公共线程池

公共池中的线程数等于处理器核心数。

但是，API 允许我们通过传递 JVM 参数来指定它将使用的线程数：

```bash
-D java.util.concurrent.ForkJoinPool.common.parallelism=4
```

重要的是要记住这是一个全局设置，它会影响所有并行流和任何其他使用公共池的 fork-join 任务。我们强烈建议不要修改此参数，除非我们有充分的理由这样做。

### 3.3. 自定义线程池

除了在默认的公共线程池中，还可以在[自定义线程池中](https://www.baeldung.com/java-8-parallel-streams-custom-threadpool)运行并行流：

```java
List<Integer> listOfNumbers = Arrays.asList(1, 2, 3, 4);
ForkJoinPool customThreadPool = new ForkJoinPool(4);
int sum = customThreadPool.submit(
    () -> listOfNumbers.parallelStream().reduce(0, Integer::sum)).get();
customThreadPool.shutdown();
assertThat(sum).isEqualTo(10);
```

请注意，Oracle 建议使用公共线程池。我们应该有充分的理由在自定义线程池中运行并行流。

## 4. 性能影响

并行处理可能有利于充分利用多个内核。但我们还需要考虑管理多线程、内存局部性、拆分源和合并结果的开销。

### 4.1. 开销

让我们看一个示例整数流。

我们将对顺序和并行归约操作运行基准测试：

```java
IntStream.rangeClosed(1, 100).reduce(0, Integer::sum);
IntStream.rangeClosed(1, 100).parallel().reduce(0, Integer::sum);
```

在这个简单的总和缩减中，将顺序流转换为并行流会导致更差的性能：

```bash
Benchmark                                                     Mode  Cnt        Score        Error  Units
SplittingCosts.sourceSplittingIntStreamParallel               avgt   25      35476,283 ±     204,446  ns/op
SplittingCosts.sourceSplittingIntStreamSequential             avgt   25         68,274 ±       0,963  ns/op
```

这背后的原因是，有时管理线程、源和结果的开销是比执行实际工作更昂贵的操作。

### 4.2. 分摊成本

平均拆分数据源是启用并行执行的必要成本，但某些数据源拆分得比其他数据源好。

让我们使用[ArrayList](https://www.baeldung.com/java-arraylist)和[LinkedList](https://www.baeldung.com/java-linkedlist)来演示：

```java
private static final List<Integer> arrayListOfNumbers = new ArrayList<>();
private static final List<Integer> linkedListOfNumbers = new LinkedList<>();

static {
    IntStream.rangeClosed(1, 1_000_000).forEach(i -> {
        arrayListOfNumbers.add(i);
        linkedListOfNumbers.add(i);
    });
}
```

我们将对两种类型的列表的顺序和并行归约操作运行基准测试：

```java
arrayListOfNumbers.stream().reduce(0, Integer::sum)
arrayListOfNumbers.parallelStream().reduce(0, Integer::sum);
linkedListOfNumbers.stream().reduce(0, Integer::sum);
linkedListOfNumbers.parallelStream().reduce(0, Integer::sum);
```

我们的结果表明，将顺序流转换为并行流只会为ArrayList带来性能优势：

```bash
Benchmark                                                     Mode  Cnt        Score        Error  Units
DifferentSourceSplitting.differentSourceArrayListParallel     avgt   25    2004849,711 ±    5289,437  ns/op
DifferentSourceSplitting.differentSourceArrayListSequential   avgt   25    5437923,224 ±   37398,940  ns/op
DifferentSourceSplitting.differentSourceLinkedListParallel    avgt   25   13561609,611 ±  275658,633  ns/op
DifferentSourceSplitting.differentSourceLinkedListSequential  avgt   25   10664918,132 ±  254251,184  ns/op
```

这背后的原因是数组可以廉价且均匀地拆分，而LinkedList没有这些属性。[TreeMap](https://www.baeldung.com/java-treemap)和[HashSet](https://www.baeldung.com/java-hashset)的拆分比LinkedList好，但不如数组。

### 4.3. 合并成本

每次我们拆分源进行并行计算时，我们还需要确保最终合并结果。

让我们在顺序流和并行流上运行基准测试，将求和和分组作为不同的合并操作：

```java
arrayListOfNumbers.stream().reduce(0, Integer::sum);
arrayListOfNumbers.stream().parallel().reduce(0, Integer::sum);
arrayListOfNumbers.stream().collect(Collectors.toSet());
arrayListOfNumbers.stream().parallel().collect(Collectors.toSet())
```

我们的结果表明，将顺序流转换为并行流只会为求和运算带来性能优势：

```bash
Benchmark                                                     Mode  Cnt        Score        Error  Units
MergingCosts.mergingCostsGroupingParallel                     avgt   25  135093312,675 ± 4195024,803  ns/op
MergingCosts.mergingCostsGroupingSequential                   avgt   25   70631711,489 ± 1517217,320  ns/op
MergingCosts.mergingCostsSumParallel                          avgt   25    2074483,821 ±    7520,402  ns/op
MergingCosts.mergingCostsSumSequential                        avgt   25    5509573,621 ±   60249,942  ns/op
```

合并操作对于某些操作来说确实很便宜，比如归约和加法，但是像分组到集合或映射这样的合并操作可能非常昂贵。

### 4.4. 内存位置

现代计算机使用复杂的多级缓存将经常使用的数据保存在处理器附近。当检测到线性内存访问模式时，硬件会在可能很快需要的假设下预取下一行数据。

当我们可以让处理器核心忙于做有用的工作时，并行会带来性能优势。由于等待缓存未命中不是有用的工作，因此我们需要将内存带宽视为一个限制因素。

让我们使用两个数组来演示这一点，一个使用原始类型，另一个使用对象数据类型：

```java
private static final int[] intArray = new int[1_000_000];
private static final Integer[] integerArray = new Integer[1_000_000];

static {
    IntStream.rangeClosed(1, 1_000_000).forEach(i -> {
        intArray[i-1] = i;
        integerArray[i-1] = i;
    });
}
```

我们将对两个数组的顺序和并行缩减操作运行基准测试：

```java
Arrays.stream(intArray).reduce(0, Integer::sum);
Arrays.stream(intArray).parallel().reduce(0, Integer::sum);
Arrays.stream(integerArray).reduce(0, Integer::sum);
Arrays.stream(integerArray).parallel().reduce(0, Integer::sum);
```

我们的结果表明，在使用基元数组时，将顺序流转换为并行流会带来更多的性能优势：

```bash
Benchmark                                                     Mode  Cnt        Score        Error  Units
MemoryLocalityCosts.localityIntArrayParallel                sequential stream  avgt   25     116247,787 ±     283,150  ns/op
MemoryLocalityCosts.localityIntArraySequential                avgt   25     293142,385 ±    2526,892  ns/op
MemoryLocalityCosts.localityIntegerArrayParallel              avgt   25    2153732,607 ±   16956,463  ns/op
MemoryLocalityCosts.localityIntegerArraySequential            avgt   25    5134866,640 ±  148283,942  ns/op
```

原语数组带来了Java中可能的最佳局部性。通常，我们的数据结构中的指针越多，我们对内存施加的压力就越大，以获取引用对象。这会对并行化产生负面影响，因为多个内核同时从内存中获取数据。

### 4.5. 网秦模式

Oracle 提供了一个简单的模型，可以帮助我们确定并行性是否可以为我们提供性能提升。在NQ模型中，N代表源数据元素的数量，而Q代表每个数据元素执行的计算量。

NQ的乘积越大，我们就越有可能从并行化中获得性能提升。对于Q非常小的问题，例如数字求和，经验法则是N应该大于 10,000。随着计算次数的增加，从并行性中获得性能提升所需的数据大小会减少。

### 4.6. 文件搜索成本

与顺序流相比，使用并行流的文件搜索性能更好。让我们在顺序和并行流上运行基准测试以搜索超过 1500 个文本文件：

```java
Files.walk(Paths.get("src/main/resources/")).map(Path::normalize).filter(Files::isRegularFile)
      .filter(path -> path.getFileName().toString().endsWith(".txt")).collect(Collectors.toList());
Files.walk(Paths.get("src/main/resources/")).parallel().map(Path::normalize).filter(Files::
      isRegularFile).filter(path -> path.getFileName().toString().endsWith(".txt")).
      collect(Collectors.toList());
```

我们的结果表明，在搜索更多文件时，将顺序流转换为并行流会带来更多的性能优势：

```java
Benchmark                                Mode  Cnt     Score         Error    Units
FileSearchCost.textFileSearchParallel    avgt   25  10808832.831 ± 446934.773  ns/op
FileSearchCost.textFileSearchSequential  avgt   25  13271799.599 ± 245112.749  ns/op
```

## 5.何时使用并行流

正如我们所见，我们在使用并行流时需要非常谨慎。

并行性可以在某些用例中带来性能优势。但是并行流不能被视为神奇的性能助推器。因此，在开发过程中仍应将顺序流用作默认值。

当我们有实际性能需求时，可以将顺序流转换为并行流。鉴于这些要求，我们应该首先运行性能测量并将并行性视为一种可能的优化策略。

大量数据和每个元素完成的许多计算表明并行性可能是一个不错的选择。

另一方面，数据量小、源分割不均匀、合并操作代价高昂和内存局部性差都表明并行执行存在潜在问题。

## 六，总结

在本文中，我们探讨了Java中顺序流和并行流之间的区别。我们了解到并行流使用默认的 fork-join 池及其工作线程。

然后我们看到并行流并不总是带来性能优势。我们考虑了管理多线程、内存局部性、拆分源和合并结果的开销。我们看到数组是并行执行的一个很好的数据源，因为它们带来了最好的局部性并且可以廉价且均匀地拆分。

最后，我们查看了NQ模型，并建议仅在我们有实际性能要求时才使用并行流。