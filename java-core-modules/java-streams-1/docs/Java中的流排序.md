## 1. 概述

在本教程中，我们将深入探讨[Java Stream API]()的不同用法如何影响流生成、处理和收集数据的顺序。

我们还将了解排序如何影响性能。

## 2. 遇到排序

简单地说，[遇到顺序](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html#Ordering)是Stream遇到数据的顺序。

### 2.1 集合源的遭遇顺序

我们选择作为源的Collection会影响Stream的相遇顺序。

为了对此进行测试，让我们简单地创建两个流。

我们的第一个是从List创建的，它有一个内在的顺序。

我们的第二个是从没有的TreeSet创建的。

然后我们将每个Stream的输出收集到一个 Array中来比较结果。

```java
@Test
public void givenTwoCollections_whenStreamedSequentially_thenCheckOutputDifferent() {
    List<String> list = Arrays.asList("B", "A", "C", "D", "F");
    Set<String> set = new TreeSet<>(list);

    Object[] listOutput = list.stream().toArray();
    Object[] setOutput = set.stream().toArray();

    assertEquals("[B, A, C, D, F]", Arrays.toString(listOutput));
    assertEquals("[A, B, C, D, F]", Arrays.toString(setOutput)); 
}
```

从我们的示例中可以看出， TreeSet 没有保持输入序列的顺序，因此打乱了Stream的遇到顺序。

如果我们的Stream是有序的，那么我们的数据是顺序处理还是并行处理都没有关系；该实现将维护Stream的相遇顺序。

当我们使用并行流重复测试时，我们得到相同的结果：

```java
@Test
public void givenTwoCollections_whenStreamedInParallel_thenCheckOutputDifferent() {
    List<String> list = Arrays.asList("B", "A", "C", "D", "F");
    Set<String> set = new TreeSet<>(list);

    Object[] listOutput = list.stream().parallel().toArray();
    Object[] setOutput = set.stream().parallel().toArray();

    assertEquals("[B, A, C, D, F]", Arrays.toString(listOutput));
    assertEquals("[A, B, C, D, F]", Arrays.toString(setOutput));
}
```

### 2.2. 删除订单

在任何时候，我们都可以使用unordered 方法显式地移除顺序约束 。

例如，让我们声明一个 TreeSet：

```java
Set<Integer> set = new TreeSet<>(
  Arrays.asList(-9, -5, -4, -2, 1, 2, 4, 5, 7, 9, 12, 13, 16, 29, 23, 34, 57, 102, 230));
```

如果我们在不调用 unordered 的情况下流式传输：

```java
set.stream().parallel().limit(5).toArray();
```

然后 TreeSet的自然顺序被保留：

```java
[-9, -5, -4, -2, 1]
```

但是，如果我们明确删除排序：

```java
set.stream().unordered().parallel().limit(5).toArray();
```

然后输出是不同的：

```java
[1, 4, 7, 9, 23]
```

原因有两方面：首先，由于顺序流一次处理一个元素的数据， 因此无序 本身几乎没有影响。然而，当我们也调用parallel时，我们影响了输出。

## 3.中间操作

我们还可以通过中间操作影响流排序。

虽然大多数中间操作将保持Stream的顺序，但有些操作会根据其性质改变它。

例如，我们可以通过排序来影响流排序：

```java
@Test
public void givenUnsortedStreamInput_whenStreamSorted_thenCheckOrderChanged() {
    List<Integer> list = Arrays.asList(-3, 10, -4, 1, 3);

    Object[] listOutput = list.stream().toArray();
    Object[] listOutputSorted = list.stream().sorted().toArray();

    assertEquals("[-3, 10, -4, 1, 3]", Arrays.toString(listOutput));
    assertEquals("[-4, -3, 1, 3, 10]", Arrays.toString(listOutputSorted));
}
```

[unordered](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/BaseStream.html#unordered()) 和[empty](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/IntStream.html#empty())是中间操作的另外两个示例，它们最终将改变 Stream 的顺序。

## 4.终端操作

最后，我们可以根据我们使用的终端操作来影响顺序。

### 4.1. ForEach 与 ForEachOrdered

ForEach 和ForEachOrdered似乎提供相同的功能，但它们有一个关键区别： ForEachOrdered 保证保持Stream的顺序。

如果我们声明一个列表：

```java
List<String> list = Arrays.asList("B", "A", "C", "D", "F");
```

并在并行化后使用 forEachOrdered ：

```java
list.stream().parallel().forEachOrdered(e -> logger.log(Level.INFO, e));
```

然后输出是有序的：

```java
INFO: B
INFO: A
INFO: C
INFO: D
INFO: F
```

但是，如果我们使用 forEach：

```java
list.stream().parallel().forEach(e -> logger.log(Level.INFO, e));
```

然后输出是 无序的：

```java
INFO: C
INFO: F
INFO: B
INFO: D
INFO: A
```

ForEach 按照元素从每个线程到达的顺序记录元素。带有 ForEachOrdered 方法的第二个 Stream 在调用log 方法之前等待前面的每个线程完成。

### 4.2. 搜集

当我们使用 collect 方法聚合Stream 输出时，请务必注意我们选择的Collection会影响顺序。

例如，本质上无序的Collections(例如 TreeSet ) 不会遵守Stream输出的顺序：

```java
@Test
public void givenSameCollection_whenStreamCollected_checkOutput() {
    List<String> list = Arrays.asList("B", "A", "C", "D", "F");

    List<String> collectionList = list.stream().parallel().collect(Collectors.toList());
    Set<String> collectionSet = list.stream().parallel()
      .collect(Collectors.toCollection(TreeSet::new)); 

    assertEquals("[B, A, C, D, F]", collectionList.toString()); 
    assertEquals("[A, B, C, D, F]", collectionSet.toString()); 
}
```

在运行我们的代码时，我们看到流的顺序通过收集到一个 集合中而改变。

### 4.3. 指定集合_

在我们使用Collectors.toMap收集无序集合的情况下，我们仍然可以通过将Collectors 方法的实现更改为使用 Linked 实现来强制排序 。

首先，我们将初始化我们的列表，以及通常的 [2 参数版本](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Collectors.html#toMap(java.util.function.Function,java.util.function.Function))的 toMap方法：

```java
@Test
public void givenList_whenStreamCollectedToHashMap_thenCheckOrderChanged() {
  List<String> list = Arrays.asList("A", "BB", "CCC");

  Map<String, Integer> hashMap = list.stream().collect(Collectors
    .toMap(Function.identity(), String::length));

  Object[] keySet = hashMap.keySet().toArray();

  assertEquals("[BB, A, CCC]", Arrays.toString(keySet));
}
```

正如预期的那样，我们的新H ashMap没有保留输入列表的原始顺序，但让我们改变它。

对于我们的第二个Stream，我们将使用 toMap 方法的 [4 参数版本](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Collectors.html#toMap(java.util.function.Function,java.util.function.Function,java.util.function.BinaryOperator,java.util.function.Supplier))告诉我们的供应商 提供一个新的LinkedHashMap：

```java
@Test
public void givenList_whenCollectedtoLinkedHashMap_thenCheckOrderMaintained(){
    List<String> list = Arrays.asList("A", "BB", "CCC");

    Map<String, Integer> linkedHashMap = list.stream().collect(Collectors.toMap(
      Function.identity(),
      String::length,
      (u, v) -> u,
      LinkedHashMap::new
    ));

    Object[] keySet = linkedHashMap.keySet().toArray();

    assertEquals("[A, BB, CCC]", Arrays.toString(keySet));
}
```

嘿，那好多了！

我们通过将数据收集到 LinkedHashMap来设法保持列表的原始顺序。

## 5.性能

如果我们使用顺序流，顺序的存在与否对我们程序的性能影响不大。然而，并行流可能会受到有序流的严重影响。

这样做的原因是每个线程都必须等待Stream的前一个元素的计算。

[让我们尝试使用Java Microbenchmark harness](https://www.baeldung.com/java-microbenchmark-harness) JMH 来证明这一点，以衡量性能。

在以下示例中，我们将使用一些常见的中间操作来衡量处理有序和无序并行流的性能成本。

### 5.1. 清楚的

让我们在有序流和无序流上使用[distinct](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/IntStream.html#distinct()) 函数来设置一个测试 。

```java
@Benchmark 
public void givenOrderedStreamInput_whenStreamDistinct_thenShowOpsPerMS() { 
    IntStream.range(1, 1_000_000).parallel().distinct().toArray(); 
}

@Benchmark
public void givenUnorderedStreamInput_whenStreamDistinct_thenShowOpsPerMS() {
    IntStream.range(1, 1_000_000).unordered().parallel().distinct().toArray();
}
```

当我们点击运行时，我们可以看到每个操作所用时间的差异：

```bash
Benchmark                        Mode  Cnt       Score   Error  Units
TestBenchmark.givenOrdered...    avgt    2  222252.283          us/op
TestBenchmark.givenUnordered...  avgt    2   78221.357          us/op

```

### 5.2. 筛选 

接下来，我们将使用带有简单[过滤器](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/IntStream.html#filter(java.util.function.IntPredicate))方法的并行Stream来返回每 10 个整数： 

```java
@Benchmark
public void givenOrderedStreamInput_whenStreamFiltered_thenShowOpsPerMS() {
    IntStream.range(1, 100_000_000).parallel().filter(i -> i % 10 == 0).toArray();
}

@Benchmark
public void givenUnorderedStreamInput_whenStreamFiltered_thenShowOpsPerMS(){
    IntStream.range(1,100_000_000).unordered().parallel().filter(i -> i % 10 == 0).toArray();
}
```

有趣的是，我们两个流之间的差异比使用distinct 方法时要小得多 。

```bash
Benchmark                        Mode  Cnt       Score   Error  Units
TestBenchmark.givenOrdered...    avgt    2  116333.431          us/op
TestBenchmark.givenUnordered...  avgt    2  111471.676          us/op
```

## 六，总结

在本文中，我们研究了 流的排序，重点关注 了Stream 过程的不同阶段 以及每个阶段如何发挥其自身的作用。

最后，我们看到了放置在 Stream上的订单合同如何影响并行流的性能。