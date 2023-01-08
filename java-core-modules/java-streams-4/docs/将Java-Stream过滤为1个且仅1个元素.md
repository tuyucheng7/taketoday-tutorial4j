## 1. 概述

在本文中，我们将使用[Collectors](https://www.baeldung.com/java-8-collectors)中的两种方法来检索与给定元素流中的特定谓词匹配的唯一元素。

对于这两种方法，我们将根据以下标准定义两种方法：

-   get 方法期望有一个唯一的结果。否则，它会抛出[异常](https://baeldung.com/java-exceptions)
-   find 方法接受结果可能会丢失，并返回一个[Optional](https://www.baeldung.com/java-optional)，如果它存在的话

## 2. 使用归约检索唯一结果

Collectors.reducing对其输入元素进行归约。为此，它应用指定为[BinaryOperator](https://www.baeldung.com/java-bifunction-interface)的函数。结果被描述为Optional。因此我们可以定义我们的查找方法。

[在我们的例子中，如果过滤](https://www.baeldung.com/java-stream-filter-lambda)后有两个或更多元素，我们只需要丢弃结果：

```java
public static <T> Optional<T> findUniqueElementMatchingPredicate_WithReduction(Stream<T> elements, Predicate<T> predicate) {
    return elements.filter(predicate)
      .collect(Collectors.reducing((a, b) -> null));
}
```

要编写 get 方法，我们需要进行以下更改：

-   如果我们检测到两个元素，我们可以直接抛出它们而不是返回 null
-   最后，我们需要获取Optional的值：[如果它是空的，我们也想抛出](https://www.baeldung.com/java-optional-throw-exception)

此外，在这种情况下，我们可以直接对[Stream](https://www.baeldung.com/java-8-streams)[应用归约](https://www.baeldung.com/java-stream-reduce)操作：

```java
public static <T> T getUniqueElementMatchingPredicate_WithReduction(Stream<T> elements, Predicate<T> predicate) {
    return elements.filter(predicate)
      .reduce((a, b) -> {
          throw new IllegalStateException("Too many elements match the predicate");
      })
      .orElseThrow(() -> new IllegalStateException("No element matches the predicate"));
}
```

## 3. 使用Collectors.collectingAndThen检索唯一结果

Collectors.collectingAndThen将函数应用于收集操作的结果列表。

因此，要定义 find 方法，我们需要获取List并且：

-   如果List有零个或两个以上的元素，则返回null
-   如果List只有一个元素，则返回它

这是此操作的代码：

```java
private static <T> T findUniqueElement(List<T> elements) {
    if (elements.size() == 1) {
        return elements.get(0);
    }
    return null;
}
```

结果，find 方法显示为：

```java
public static <T> Optional<T> findUniqueElementMatchingPredicate_WithCollectingAndThen(Stream<T> elements, Predicate<T> predicate) {
    return elements.filter(predicate)
      .collect(Collectors.collectingAndThen(Collectors.toList(), list -> Optional.ofNullable(findUniqueElement(list))));
}
```

为了使我们的私有方法适应 get 的情况，如果检索到的元素的数量不正好为 1，我们需要抛出。让我们准确区分没有结果和结果太多的情况，就像我们所做的那样减少：

```java
private static <T> T getUniqueElement(List<T> elements) {
    if (elements.size() > 1) {
        throw new IllegalStateException("Too many elements match the predicate");
    } else if (elements.size() == 0) {
        throw new IllegalStateException("No element matches the predicate");
    }
    return elements.get(0);
}
```

最后，鉴于我们将类命名为FilterUtils，我们可以编写 get 方法：

```java
public static <T> T getUniqueElementMatchingPredicate_WithCollectingAndThen(Stream<T> elements, Predicate<T> predicate) {
    return elements.filter(predicate)
      .collect(Collectors.collectingAndThen(Collectors.toList(), FilterUtils::getUniqueElement));
}
```

## 4.性能基准

让我们使用[JMH](https://www.baeldung.com/java-microbenchmark-harness)来运行不同方法之间的快速性能比较。

首先，让我们将我们的方法应用到

-   包含从 1 到 100 万[的所有](https://www.baeldung.com/java-intstream-convert)[整数](https://www.baeldung.com/java-intstream-convert)的[Stream](https://www.baeldung.com/java-intstream-convert)
-   验证元素是否等于 751879的[谓词](https://www.baeldung.com/java-predicate-chain)

在这种情况下，Predicate将针对Stream的一个唯一元素进行验证。让我们看一下Benchmark的定义：

```java
@State(Scope.Benchmark)
public static class MyState {
    final Stream<Integer> getIntegers() { 
        return IntStream.range(1, 1000000).boxed();
    }
    
    final Predicate<Integer> PREDICATE = i -> i == 751879;
}

@Benchmark
public void evaluateFindUniqueElementMatchingPredicate_WithReduction(Blackhole blackhole, MyState state) {
    blackhole.consume(FilterUtils.findUniqueElementMatchingPredicate_WithReduction(state.INTEGERS.stream(), state.PREDICATE));
}

@Benchmark
public void evaluateFindUniqueElementMatchingPredicate_WithCollectingAndThen(Blackhole blackhole, MyState state) {
    blackhole.consume(FilterUtils.findUniqueElementMatchingPredicate_WithCollectingAndThen(state.INTEGERS.stream(), state.PREDICATE));
}

@Benchmark
public void evaluateGetUniqueElementMatchingPredicate_WithReduction(Blackhole blackhole, MyState state) {
    try {
        FilterUtils.getUniqueElementMatchingPredicate_WithReduction(state.INTEGERS.stream(), state.PREDICATE);
    } catch (IllegalStateException exception) {
        blackhole.consume(exception);
    }
}

@Benchmark
public void evaluateGetUniqueElementMatchingPredicate_WithCollectingAndThen(Blackhole blackhole, MyState state) {
    try {
        FilterUtils.getUniqueElementMatchingPredicate_WithCollectingAndThen(state.INTEGERS.stream(), state.PREDICATE);
    } catch (IllegalStateException exception) {
        blackhole.consume(exception);
    }
}
```

让我们运行它。我们正在测量每秒的操作数。越高越好：

```plaintext
Benchmark                                                                          Mode  Cnt    Score    Error  Units
BenchmarkRunner.evaluateFindUniqueElementMatchingPredicate_WithCollectingAndThen  thrpt   25  140.581 ± 28.793  ops/s
BenchmarkRunner.evaluateFindUniqueElementMatchingPredicate_WithReduction          thrpt   25  100.171 ± 36.796  ops/s
BenchmarkRunner.evaluateGetUniqueElementMatchingPredicate_WithCollectingAndThen   thrpt   25  145.568 ±  5.333  ops/s
BenchmarkRunner.evaluateGetUniqueElementMatchingPredicate_WithReduction           thrpt   25  144.616 ± 12.917  ops/s
```

正如我们所见，在这种情况下，不同方法的表现非常相似。

让我们更改Predicate以检查Stream的元素是否等于 0。对于List的所有元素，此条件均为假。我们现在可以再次运行基准测试：

```plaintext
Benchmark                                                                          Mode  Cnt    Score    Error  Units
BenchmarkRunner.evaluateFindUniqueElementMatchingPredicate_WithCollectingAndThen  thrpt   25  165.751 ± 19.816  ops/s
BenchmarkRunner.evaluateFindUniqueElementMatchingPredicate_WithReduction          thrpt   25  174.667 ± 20.909  ops/s
BenchmarkRunner.evaluateGetUniqueElementMatchingPredicate_WithCollectingAndThen   thrpt   25  188.293 ± 18.348  ops/s
BenchmarkRunner.evaluateGetUniqueElementMatchingPredicate_WithReduction           thrpt   25  196.689 ±  4.155  ops/s
```

同样，性能图表非常平衡。

最后，让我们看看如果我们使用一个Predicate对大于 751879 的值返回 true 会发生什么： List中有大量元素匹配这个Predicate。这导致以下基准：

```plaintext
Benchmark                                                                          Mode  Cnt    Score    Error  Units
BenchmarkRunner.evaluateFindUniqueElementMatchingPredicate_WithCollectingAndThen  thrpt   25   70.879 ±  6.205  ops/s
BenchmarkRunner.evaluateFindUniqueElementMatchingPredicate_WithReduction          thrpt   25  210.142 ± 23.680  ops/s
BenchmarkRunner.evaluateGetUniqueElementMatchingPredicate_WithCollectingAndThen   thrpt   25   83.927 ±  1.812  ops/s
BenchmarkRunner.evaluateGetUniqueElementMatchingPredicate_WithReduction           thrpt   25  252.881 ±  2.710  ops/s
```

正如我们所见，减少的变体更有效。此外，直接在过滤后的Stream上使用reduce会大放异彩，因为在找到两个匹配值后会直接抛出Exception 。

简而言之，如果性能很重要：

-   使用减少应该受到青睐
-   如果我们期望找到很多潜在的匹配值，减少Stream的 get 方法会更快

## 5.总结

在本教程中，我们看到了在过滤Stream后检索唯一结果的不同方法，然后比较了它们的效率。