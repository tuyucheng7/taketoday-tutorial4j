## 1. 概述

在本教程中，我们将重点关注ArrayList和Vector类之间的区别。它们都属于JavaCollections Framework 并实现了java.util.List接口。

然而，这些类在它们的实现上有很大的不同。

## 2.有什么不同？

作为快速入门，让我们介绍ArrayList和Vector 的主要区别。然后，我们将更详细地讨论一些要点：

-   同步——这两者之间的第一个主要区别。Vector是同步的，而ArrayList 不是。
-   大小增长——两者之间的另一个区别是它们在达到容量时调整大小的方式。Vector将其大小加倍。相比之下，ArrayList 仅增加其长度的一半
-   迭代 ——Vector可以使用Iterator和Enumeration来遍历元素。 另一方面，ArrayList只能使用 Iterator。
-   性能——主要是由于同步， Vector操作比ArrayList慢
-   框架——另外， ArrayList是集合框架的一部分，是在 JDK 1.2 中引入的。同时， Vector 作为遗留类存在于早期版本的Java中。

## 3.矢量图

由于我们已经有了关于[ArrayList 的扩展指南，](https://www.baeldung.com/java-arraylist)因此 我们不会在这里讨论它的 API 和功能。另一方面，我们将介绍有关Vector的一些核心细节。 

简单地说，Vector是 一个可调整大小的数组。它可以随着我们添加或删除元素而增长和缩小。

我们可以用典型的方式创建一个向量：

```java
Vector<String> vector = new Vector<>();
```

默认构造函数创建一个初始容量为 10的空Vector 。

让我们添加一些值：

```java
vector.add("baeldung");
vector.add("Vector");
vector.add("example");
```

[最后，让我们使用Iterator](https://www.baeldung.com/java-iterator)接口遍历这些值：

```java
Iterator<String> iterator = vector.iterator();
while (iterator.hasNext()) {
    String element = iterator.next();
    // ...
}
```

或者，我们可以使用 Enumeration遍历Vector：

```java
Enumeration e = vector.elements();
while(e.hasMoreElements()) {
    String element = e.nextElement();
    // ... 
}
```

现在，让我们更深入地探索它们的一些独特功能。

## 4.并发

我们已经提到ArrayList和Vector在并发策略上是不同的，但让我们仔细看看。如果我们深入研究Vector 的方法签名，我们会看到每个方法都有同步关键字：

```java
public synchronized E get(int index)
```

简单地说，这意味着一次只有一个线程可以访问给定的向量。

不过，实际上，这种操作级同步无论如何都需要用我们自己的复合操作同步来覆盖。

所以相比之下，ArrayList采取了不同的做法。它的方法 不是 同步的，并且该问题被分离到专门用于并发的类中。

例如，我们可以使用 [CopyOnWriteArrayList ](https://www.baeldung.com/java-copy-on-write-arraylist)或 [Collections.synchronizedList](https://www.baeldung.com/java-synchronized-collections) 来获得与 Vector类似的效果：

```java
vector.get(1); // synchronized
Collections.synchronizedList(arrayList).get(1); // also synchronized
```

## 5.性能

正如我们上面已经讨论过的，Vector是同步的，这会直接影响性能。

要查看Vector 与ArrayList 操作之间的性能差异 ，让我们编写一个简单的[JMH 基准](https://www.baeldung.com/java-microbenchmark-harness)测试。

过去，我们已经了解 了[ArrayList](https://www.baeldung.com/java-collections-complexity)[操作](https://www.baeldung.com/java-collections-complexity)[的时间复杂度，所以让我们添加](https://www.baeldung.com/java-collections-complexity)Vector的测试用例。

首先，让我们测试get()方法：

```java
@Benchmark
public Employee testGet(ArrayListBenchmark.MyState state) {
    return state.employeeList.get(state.employeeIndex);
}

@Benchmark
public Employee testVectorGet(ArrayListBenchmark.MyState state) {
    return state.employeeVector.get(state.employeeIndex);
}
```

我们将配置 JMH 以使用三个线程和 10 次预热迭代。

并且，让我们报告纳秒级别的每次操作的平均时间：

```plaintext
Benchmark                         Mode  Cnt   Score   Error  Units
ArrayListBenchmark.testGet        avgt   20   9.786 ± 1.358  ns/op
ArrayListBenchmark.testVectorGet  avgt   20  37.074 ± 3.469  ns/op
```

我们可以看到 ArrayList#get的工作速度大约是Vector#get的三倍。

现在，让我们比较一下 contains()操作的结果：

```java
@Benchmark
public boolean testContains(ArrayListBenchmark.MyState state) {
    return state.employeeList.contains(state.employee);
}

@Benchmark
public boolean testContainsVector(ArrayListBenchmark.MyState state) {
    return state.employeeVector.contains(state.employee);
}
```

并将结果打印出来：

```plaintext
Benchmark                              Mode  Cnt  Score   Error  Units
ArrayListBenchmark.testContains        avgt   20  8.665 ± 1.159  ns/op
ArrayListBenchmark.testContainsVector  avgt   20  36.513 ± 1.266  ns/op
```

正如我们所看到的，对于contains()操作，Vector的执行时间比ArrayList长得多 。

## 6.总结

在本文中，我们了解了Java中Vector和ArrayList类之间的区别。此外，我们还更详细地介绍了Vector功能。