## 1. 概述

在本快速教程中，我们将比较两个Arrays.sort(Object[])和 Arrays.sort(int[]) [排序](https://www.baeldung.com/java-sorting)操作。

首先，我们将分别描述每种方法。之后，我们将编写性能测试来测量它们的运行时间。

## 2. Arrays.sort(对象[])

在我们继续之前，重要的是要记住 [Arrays.sort()](https://www.baeldung.com/java-util-arrays) 适用于原始类型和引用类型数组。

Arrays.sort(Object[])接受引用类型。

例如，我们有一个Integer 对象数组：

```java
Integer[] numbers = {5, 22, 10, 0};
```

要对数组进行排序，我们可以简单地使用：

```java
Arrays.sort(numbers);
```

现在，数字数组的所有元素都按升序排列：

```plaintext
[0, 5, 10, 22]
```

Arrays.sort(Object[]) 基于 TimSort 算法，时间复杂度为 O(n log(n))。简而言之，TimSort 使用了[插入](https://www.baeldung.com/java-merge-sort)[排序](https://www.baeldung.com/java-insertion-sort)和合并排序算法。但是，与其他排序算法(如某些 QuickSort 实现)相比，它仍然较慢。

## 3. 数组排序(int[])

另一方面，Arrays.sort(int[])使用原始int数组。

同样，我们可以定义一个int[]原语数组：

```java
int[] primitives = {5, 22, 10, 0};
```

并使用Arrays.sort(int[])的另一个实现对其进行排序。这一次，接受一组原语：

```java
Arrays.sort(primitives);
```

此操作的结果与前面的示例没有什么不同。primitives数组中的项目将如下所示：

```plaintext
[0, 5, 10, 22]
```

在引擎盖下，它使用双轴快速[排序算法](https://www.baeldung.com/java-quicksort)。它在 JDK 10 中的内部实现通常比传统的单轴快速排序更快。

该算法提供O(n log(n))平均[时间复杂度](https://www.baeldung.com/java-algorithm-complexity)。对于许多收藏来说，这是一个很好的平均排序时间。此外，它具有完全就位的优势，因此不需要任何额外的存储空间。

虽然，在最坏的情况下，它的时间复杂度是O(n 2 )。 

## 4.时间比较

那么，哪种算法更快，为什么？让我们先做一些理论，然后我们将使用 JMH 运行一些具体的测试。

### 4.1. 定性分析

由于几个不同的原因， Arrays.sort(Object[])通常比Arrays.sort(int[])慢。

首先是不同的算法。QuickSort 通常比 Timsort 更快。

其次是每种方法如何比较这些值。

看，由于Arrays.sort(Object[])需要将一个对象与另一个对象进行比较，因此它需要调用每个元素的compareTo方法。至少，除了比较操作实际是什么之外，这还需要方法查找并将调用压入堆栈。

另一方面，Arrays.sort(int[])可以简单地使用像<和>这样的原始关系运算符，它们是单字节码指令。

### 4.2. JMH 参数

最后，让我们找出哪种排序方法对实际数据运行得更快。为此，我们将使用 [JMH](https://www.baeldung.com/java-microbenchmark-harness)(Java Microbenchmark Harness)工具来编写我们的基准测试。

因此，我们将在这里做一个非常简单的基准测试。它并不全面，但会让我们了解如何比较Arrays.sort(int[])和 Arrays.sort( Integer[] )排序方法。

在我们的基准测试类中，我们将使用配置注解：

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(batchSize = 100000, iterations = 10)
@Warmup(batchSize = 100000, iterations = 10)
public class ArraySortBenchmark {
}
```

在这里，我们要测量单个操作的平均时间 ( Mode.AverageTime)并以毫秒为单位显示我们的结果 ( TimeUnit.MILLISECONDS)。此外，通过batchSize参数，我们告诉 JMH 执行 100,000 次迭代以确保我们的结果具有高精度。

### 4.3. 基准测试

在运行测试之前，我们需要定义要排序的数据容器：

```java
@State(Scope.Thread)
public static class Initialize {
    Integer[] numbers = {-769214442, -1283881723, 1504158300, -1260321086, -1800976432, 1278262737, 
      1863224321, 1895424914, 2062768552, -1051922993, 751605209, -1500919212, 2094856518, 
      -1014488489, -931226326, -1677121986, -2080561705, 562424208, -1233745158, 41308167 };
    int[] primitives = {-769214442, -1283881723, 1504158300, -1260321086, -1800976432, 1278262737, 
      1863224321, 1895424914, 2062768552, -1051922993, 751605209, -1500919212, 2094856518, 
      -1014488489, -931226326, -1677121986, -2080561705, 562424208, -1233745158, 41308167};
}
```

让我们选择Integer[] 数字 和原始元素的int[] 原始数组。@State注解表明类中声明的变量不会成为运行基准测试的一部分。但是，我们可以在我们的基准测试方法中使用它们。

现在，我们准备为Arrays.sort(Integer[])添加第一个微基准：

```java
@Benchmark
public Integer[] benchmarkArraysIntegerSort(ArraySortBenchmark.Initialize state) {
    Arrays.sort(state.numbers);
    return state.numbers;
}
```

接下来，对于Arrays.sort(int[])：

```java
@Benchmark
public int[] benchmarkArraysIntSort(ArraySortBenchmark.Initialize state) {
    Arrays.sort(state.primitives);
    return state.primitives;
}
```

### 4.4. 试验结果

最后，我们运行测试并比较结果：

```java
Benchmark                   Mode  Cnt  Score   Error  Units
benchmarkArraysIntSort      avgt   10  1.095 ± 0.022  ms/op
benchmarkArraysIntegerSort  avgt   10  3.858 ± 0.060  ms/op
```

从结果中，我们可以看到Arrays.sort(int[]) 方法在我们的测试中比Arrays.sort(Object[ ]) 方法执行得更好，这可能是由于我们之前确定的原因。

尽管这些数字似乎支持我们的理论，但我们需要对更多种类的输入进行测试才能获得更好的想法。

另外， 请记住，我们在这里提供的数字只是 JMH 基准测试结果——因此我们应该始终在我们自己的系统和运行时范围内进行测试。

### 4.5. 为什么选择 Timsort？

那么我们或许应该问自己一个问题。如果 QuickSort 更快，为什么不将它用于两种实现？

看，QuickSort不稳定，所以我们不能用它来对Objects进行排序。基本上，如果两个 int相等，那么它们的相对顺序保持不变并不重要，因为一个 2 与另一个2 没有区别 。但是对于对象，我们可以按一个属性然后按另一个属性排序，从而使起始顺序很重要.

## 5.总结

在本文中，我们比较了Java中可用的两种排序方法： Arrays.sort(int[])和 Arrays.sort( Integer[] )。此外，我们还讨论了在它们的实现中使用的排序算法。

最后，在基准性能测试的帮助下，我们展示了每个 排序选项的示例运行时间。