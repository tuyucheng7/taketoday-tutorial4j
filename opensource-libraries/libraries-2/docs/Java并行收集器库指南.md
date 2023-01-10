## 1. 简介

[Parallel-collectors](https://github.com/pivovarit/parallel-collectors)是一个小型库，它提供一组支持并行处理的JavaStream API 收集器，同时规避了标准并行流的主要缺陷。

## 2.Maven依赖

如果我们想开始使用这个库，我们需要在 Maven 的pom.xml文件中添加一个条目：

```xml
<dependency>
    <groupId>com.pivovarit</groupId>
    <artifactId>parallel-collectors</artifactId>
    <version>1.1.0</version>
</dependency>
```

或者 Gradle 构建文件中的一行：

```xml
compile 'com.pivovarit:parallel-collectors:1.1.0'
```

最新版本[可以在 Maven Central 上找到](https://search.maven.org/search?q=g:com.pivovarit AND a:parallel-collectors&core=gav)。

## 3. 并行流注意事项

Parallel Streams 是Java8 的亮点之一，但事实证明它们只适用于繁重的 CPU 处理。

这样做的原因是，Parallel Streams 由 JVM 范围内共享的ForkJoinPool内部支持，它提供有限的并行性，并由在单个 JVM 实例上运行的所有 Parallel Streams 使用。

例如，假设我们有一个 id 列表，我们想用它们来获取用户列表，而这个操作是昂贵的。

我们可以为此使用并行流：

```java
List<Integer> ids = Arrays.asList(1, 2, 3); 
List<String> results = ids.parallelStream() 
  .map(i -> fetchById(i)) // each operation takes one second
  .collect(Collectors.toList()); 

System.out.println(results); // [user-1, user-2, user-3]
```

事实上，我们可以看到有明显的加速。但是，如果我们开始运行多个并行阻塞操作……就会出现问题。这可能会很快使池饱和并导致潜在的巨大延迟。这就是为什么通过创建单独的线程池来构建隔板很重要——以防止不相关的任务影响彼此的执行。

为了提供自定义的 ForkJoinPool实例，我们可以利用[这里描述的技巧](https://www.baeldung.com/java-8-parallel-streams-custom-threadpool)，但这种方法依赖于未记录的 hack，并且在 JDK10 之前是错误的。我们可以在问题本身中阅读更多内容 – [[JDK8190974]](https://bugs.openjdk.java.net/browse/JDK-8190974)。

## 4. 并行收集器的实际应用

顾名思义，并行收集器只是标准的 Stream API 收集器，允许在collect()阶段并行执行其他操作。

ParallelCollectors (它反映了Collectors类)类是一个外观，提供对库的全部功能的访问。

如果我们想重做上面的例子，我们可以简单地写：

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

List<Integer> ids = Arrays.asList(1, 2, 3);

CompletableFuture<List<String>> results = ids.stream()
  .collect(ParallelCollectors.parallelToList(i -> fetchById(i), executor, 4));

System.out.println(results.join()); // [user-1, user-2, user-3]
```

结果是一样的，但是，我们能够提供我们的自定义线程池，指定我们的自定义并行度级别，并且结果包装在[CompletableFuture](https://www.baeldung.com/java-completablefuture)实例中，而不会阻塞当前线程。 

另一方面，标准并行流无法实现其中任何一个。

## 4.1. 并行收集器.parallelToList/ToSet()

最直观的是，如果我们想并行处理Stream并将结果收集到List或Set中，我们可以简单地使用ParallelCollectors.parallelToList或parallelToSet：

```java
List<Integer> ids = Arrays.asList(1, 2, 3);

List<String> results = ids.stream()
  .collect(parallelToList(i -> fetchById(i), executor, 4))
  .join();
```

## 4.2. ParallelCollectors.parallelToMap()

如果我们想将Stream元素收集到一个Map实例中，就像使用 Stream API 一样，我们需要提供两个映射器：

```java
List<Integer> ids = Arrays.asList(1, 2, 3);

Map<Integer, String> results = ids.stream()
  .collect(parallelToMap(i -> i, i -> fetchById(i), executor, 4))
  .join(); // {1=user-1, 2=user-2, 3=user-3}
```

我们还可以提供自定义Map实例Supplier：

```java
Map<Integer, String> results = ids.stream()
  .collect(parallelToMap(i -> i, i -> fetchById(i), TreeMap::new, executor, 4))
  .join();

```

以及自定义冲突解决策略：

```java
List<Integer> ids = Arrays.asList(1, 2, 3);

Map<Integer, String> results = ids.stream()
  .collect(parallelToMap(i -> i, i -> fetchById(i), TreeMap::new, (s1, s2) -> s1, executor, 4))
  .join();
```

## 4.3. ParallelCollectors.parallelToCollection()

与上面类似，如果我们想获得打包在我们自定义容器中的结果，我们可以通过我们的自定义Collection Supplier ：

```java
List<String> results = ids.stream()
  .collect(parallelToCollection(i -> fetchById(i), LinkedList::new, executor, 4))
  .join();
```

## 4.4. ParallelCollectors.parallelToStream()

如果以上还不够，我们实际上可以获取一个Stream实例并在那里继续自定义处理：

```java
Map<Integer, List<String>> results = ids.stream()
  .collect(parallelToStream(i -> fetchById(i), executor, 4))
  .thenApply(stream -> stream.collect(Collectors.groupingBy(i -> i.length())))
  .join();
```

## 4.5. ParallelCollectors.parallel()

这允许我们按完成顺序流式传输结果：

```java
ids.stream()
  .collect(parallel(i -> fetchByIdWithRandomDelay(i), executor, 4))
  .forEach(System.out::println);

// user-1
// user-3
// user-2

```

在这种情况下，我们可以预期收集器每次都会返回不同的结果，因为我们引入了随机处理延迟。

## 4.6. ParallelCollectors.parallelOrdered()

此工具允许像上面一样流式传输结果，但保持原始顺序：

```java
ids.stream()
  .collect(parallelOrdered(i -> fetchByIdWithRandomDelay(i), executor, 4))
  .forEach(System.out::println);

// user-1
// user-2 
// user-3 

```

在这种情况下，收集器将始终保持顺序但可能比上面的慢。

## 5. 限制

在撰写本文时，即使使用了短路操作，并行收集器也无法处理无限流——这是 Stream API 内部机制强加的设计限制。简而言之，Stream将收集器视为非短路操作，因此流需要在终止之前处理所有上游元素。

另一个限制是短路操作不会中断短路后的剩余任务。

## 六. 总结

我们看到了并行收集器库如何允许我们通过使用自定义JavaStream API收集器和CompletableFutures来执行并行处理，以利用自定义线程池、并行性和 CompletableFutures 的非阻塞样式。

与往常一样，代码片段可[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/libraries-2)获得。

如需进一步阅读，请参阅GitHub 上的[并行收集器库、](https://github.com/pivovarit/parallel-collectors)[作者的博客](http://4comprehension.com/)和作者的[Twitter 帐户](https://twitter.com/pivovarit)。