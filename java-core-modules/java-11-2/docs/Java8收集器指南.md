## 1. 概述

在本教程中，我们将介绍Java 8的收集器，它们用于处理Stream的最后一步。

要了解有关Stream API 本身的更多信息，我们可以查看[这篇](https://www.baeldung.com/java-8-streams)文章。

如果我们想了解如何利用收集器的强大功能进行并行处理，可以查看[这个](https://github.com/pivovarit/parallel-collectors)项目。

## 2. Stream.collect()方法

Stream.collect()是Java 8的Stream API的终端方法之一。它允许我们对Stream实例中保存的数据元素执行可变折叠操作(将元素重新打包到某些数据结构并应用一些额外的逻辑，将它们拼接起来等)。

此操作的策略是通过Collector接口实现提供的。

## 3. Collectors

所有预定义的实现都可以在Collectors类中找到，通常的做法是对它们使用以下静态导入来提高可读性：

```java
import static java.util.stream.Collectors.*;
```

我们还可以使用我们选择的单个导入收集器：

```java
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
```

在以下示例中，我们将重用以下列表：

```java
List<String> givenList = Arrays.asList("a", "bb", "ccc", "dd");
```

### 3.1 Collectors.toList()

toList收集器可用于将所有Stream元素收集到List实例中。要记住的重要一点是，我们不能假设任何特定的List实现都使用此方法。如果我们想对此有更多的控制，我们可以使用toCollection来代替。

让我们创建一个表示一系列元素的Stream实例，然后将它们收集到一个List实例中：

```java
List<String> result = givenList.stream()
    .collect(toList());
```

#### 3.1.1 Collectors.toUnmodifiableList()

Java 10引入了一种方便的方法来将Stream元素累积到[不可修改的List中](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html#unmodifiable)：

```java
List<String> result = givenList.stream()
    .collect(toUnmodifiableList());
```

现在，如果我们尝试修改结果List，我们将得到UnsupportedOperationException：

```java
assertThatThrownBy(() -> result.add("foo"))
    .isInstanceOf(UnsupportedOperationException.class);
```

### 3.2 Collectors.toSet()

toSet收集器可用于将所有Stream元素收集到Set实例中。要记住的重要一点是，我们不能使用此方法假设任何特定的Set实现。如果我们想对此有更多的控制，我们可以使用toCollection来代替。

让我们创建一个表示一系列元素的Stream实例，然后将它们收集到一个Set实例中：

```java
Set<String> result = givenList.stream()
    .collect(toSet());
```

Set不包含重复的元素。如果我们的集合包含彼此相等的元素，则它们仅在结果集合中出现一次：

```java
List<String> listWithDuplicates = Arrays.asList("a", "bb", "c", "d", "bb");
Set<String> result = listWithDuplicates.stream().collect(toSet());
assertThat(result).hasSize(4);
```

#### 3.2.1 Collectors.toUnmodifiableSet()

从Java 10开始，我们可以使用toUnmodifiableSet()收集器轻松创建[不可修改的Set](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html#unmodifiable)：

```java
Set<String> result = givenList.stream()
    .collect(toUnmodifiableSet());
```

任何修改结果集合的尝试都会以UnsupportedOperationException告终：

```java
assertThatThrownBy(() -> result.add("foo"))
    .isInstanceOf(UnsupportedOperationException.class);
```

### 3.3 Collectors.toCollection()

正如我们已经注意到的，当使用toSet和toList收集器时，我们不能对它们的实现做出任何假设。如果我们想使用自定义实现，我们需要使用toCollection收集器和我们选择的提供的集合。

让我们创建一个表示一系列元素的Stream实例，然后将它们收集到一个LinkedList实例中：

```java
List<String> result = givenList.stream()
    .collect(toCollection(LinkedList::new))
```

请注意，这不适用于任何不可变集合。在这种情况下，我们需要编写自定义收集器实现或使用collectAndThen。

### 3.4 Collectors.toMap()

toMap收集器可用于将Stream元素收集到Map实例中。为此，我们需要提供两个函数：

-   keyMapper
-   valueMapper

我们将使用keyMapper从Stream元素中提取Map键，并使用valueMapper提取与给定键关联的值。

让我们将这些元素收集到一个Map中，该Map将字符串存储为键，将它们的长度存储为值：

```java
Map<String, Integer> result = givenList.stream()
    .collect(toMap(Function.identity(), String::length))
```

Function.identity()只是定义接受和返回相同值的函数的快捷方式。

那么如果我们的集合包含重复的元素会怎样呢？与toSet相反，toMap不会静默过滤重复项，这是可以理解的，因为它如何确定为该键选择哪个值？

```java
List<String> listWithDuplicates = Arrays.asList("a", "bb", "c", "d", "bb");
assertThatThrownBy(() -> {
    listWithDuplicates.stream().collect(toMap(Function.identity(), String::length));
}).isInstanceOf(IllegalStateException.class);
```

请注意，toMap甚至不评估这些值是否也相等。如果它看到重复的键，它会立即抛出IllegalStateException。

在这种键冲突的情况下，我们应该使用带有另一个签名的toMap：

```java
Map<String, Integer> result = givenList.stream()
    .collect(toMap(Function.identity(), String::length, (item, identicalItem) -> item));
```

这里的第三个参数是BinaryOperator，我们可以在其中指定我们希望如何处理冲突。在这种情况下，我们将只选择这两个冲突值中的任何一个，因为我们知道相同的字符串也总是具有相同的长度。

#### 3.4.1 Collectors.toUnmodifiableMap()

与List和Set类似，Java 10引入了一种将Stream元素收集到不可[修改的Map](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/Map.html#unmodifiable)中的简单方法：

```java
Map<String, Integer> result = givenList.stream()
    .collect(toUnmodifiableMap(Function.identity(), String::length))
```

如我们所见，如果我们尝试将新条目放入结果Map中，我们将得到UnsupportedOperationException：

```java
assertThatThrownBy(() -> result.put("foo", 3))
    .isInstanceOf(UnsupportedOperationException.class);
```

### 3.5 Collectors.collectingAndThen()

CollectingAndThen是一个特殊的收集器，它允许我们在收集结束后立即对结果执行另一个操作。

让我们将Stream元素收集到一个List实例中，然后将结果转换为一个ImmutableList实例：

```java
List<String> result = givenList.stream()
    .collect(collectingAndThen(toList(), ImmutableList::copyOf))
```

### 3.6 Collectors.joining ()

加入收集器可用于加入Stream<String\>元素。

我们可以通过以下方式将它们连接在一起：

```java
String result = givenList.stream()
    .collect(joining());
```

这将导致：

```java
"abbcccdd"
```

我们还可以指定自定义分隔符、前缀、后缀：

```java
String result = givenList.stream()
    .collect(joining(" "));
```

这将导致：

```java
"a bb ccc dd"
```

我们也可以这样写：

```java
String result = givenList.stream()
    .collect(joining(" ", "PRE-", "-POST"));
```

这将导致：

```java
"PRE-a bb ccc dd-POST"
```

### 3.7 Collectors.counting()

Counting是一个简单的收集器，它允许对所有Stream元素进行计数。

现在我们可以写：

```java
Long result = givenList.stream()
    .collect(counting());
```

### 3.8 Collectors.summarizingDouble/Long/Int()

SummarizingDouble/Long/Int是一个收集器，它返回一个特殊的类，其中包含有关提取元素流中数值数据的统计信息。

我们可以通过执行以下操作获取有关字符串长度的信息：

```java
DoubleSummaryStatistics result = givenList.stream()
    .collect(summarizingDouble(String::length));
```

在这种情况下，以下情况将成立：

```java
assertThat(result.getAverage()).isEqualTo(2);
assertThat(result.getCount()).isEqualTo(4);
assertThat(result.getMax()).isEqualTo(3);
assertThat(result.getMin()).isEqualTo(1);
assertThat(result.getSum()).isEqualTo(8);
```

### 3.9 Collectors.averagingDouble/Long/Int()

AveragingDouble/Long/Int是一个收集器，它只返回提取元素的平均值。

我们可以通过执行以下操作获得平均字符串长度：

```java
Double result = givenList.stream()
    .collect(averagingDouble(String::length));
```

### 3.10 Collectors.summingDouble/Long/Int()

SummingDouble/Long/Int是一个收集器，它只返回提取元素的总和。

我们可以通过以下方式获得所有字符串长度的总和：

```java
Double result = givenList.stream()
    .collect(summingDouble(String::length));
```

### 3.11 Collectors.maxBy()/minBy()

MaxBy/MinBy收集器根据提供的Comparator实例返回Stream的最大/最小元素。

我们可以通过以下方式选择最大的元素：

```java
Optional<String> result = givenList.stream()
    .collect(maxBy(Comparator.naturalOrder()));
```

我们可以看到返回的值被包裹在一个Optional实例中。这迫使用户重新考虑空集合角落的情况。

### 3.12 Collectors.groupingBy()

GroupingBy收集器用于按某些属性对对象进行分组，然后将结果存储在Map实例中。

我们可以按字符串长度对它们进行分组，并将分组结果存储在Set实例中：

```java
Map<Integer, Set<String>> result = givenList.stream()
    .collect(groupingBy(String::length, toSet()));
```

这将导致以下情况成立：

```java
assertThat(result)
    .containsEntry(1, newHashSet("a"))
    .containsEntry(2, newHashSet("bb", "dd"))
    .containsEntry(3, newHashSet("ccc"));
```

我们可以看到groupingBy方法的第二个参数是一个Collector。此外，我们可以自由使用我们选择的任何收集器。

### 3.13 Collectors.partitioningBy()

PartitioningBy是groupingBy的一种特殊情况，它接收Predicate实例，然后将Stream元素收集到Map实例中，该实例将Boolean值存储为键，将集合存储为值。在“true”键下，我们可以找到与给定Predicate匹配的元素集合，在“false”键下，我们可以找到与给定Predicate不匹配的元素集合。

我们可以写：

```java
Map<Boolean, List<String>> result = givenList.stream()
    .collect(partitioningBy(s -> s.length() > 2))
```

这会产生一个包含以下内容的Map：

```java
{false=["a", "bb", "dd"], true=["ccc"]}

```

### 3.14 Collectors.teeing()

让我们使用我们目前学到的收集器从给定的Stream中找到最大和最小数字：

```java
List<Integer> numbers = Arrays.asList(42, 4, 2, 24);
Optional<Integer> min = numbers.stream().collect(minBy(Integer::compareTo));
Optional<Integer> max = numbers.stream().collect(maxBy(Integer::compareTo));
// do something useful with min and max
```

在这里，我们使用了两个不同的收集器，然后结合这两个的结果来创造一些有意义的东西。在Java 12之前，为了覆盖这样的用例，我们必须对给定的Stream进行两次操作，将中间结果存储到临时变量中，然后再组合这些结果。

幸运的是，Java 12提供了一个内置的收集器来代表我们处理这些步骤；我们所要做的就是提供两个收集器和组合器功能。

由于这个新的收集器将给定的流向两个不同的方向发球，因此称为[teeing](https://en.wikipedia.org/wiki/Tee_(command))：

```java
numbers.stream().collect(teeing(
    minBy(Integer::compareTo), // The first collector
    maxBy(Integer::compareTo), // The second collector
    (min, max) -> // Receives the result from those collectors and combines them
));
```

此示例在GitHub上的[java-12](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/core-java-modules/core-java-12)项目中可用。

## 4. 自定义收集器

如果我们想编写自己的Collector实现，我们需要实现Collector接口，并指定它的三个泛型参数：

```java
public interface Collector<T, A, R> {...}
```

1.  T – 可用于收集的对象类型
2.  A – 可变累加器对象的类型
3.  R – 最终结果的类型

让我们编写一个示例收集器，用于将元素收集到ImmutableSet实例中。我们首先指定正确的类型：

```java
private class ImmutableSetCollector<T>
    implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {...}
```

由于我们需要一个可变集合来处理内部集合操作，因此我们不能使用ImmutableSet。相反，我们需要使用其他一些可变集合，或者任何其他可以临时为我们积累对象的类。在这种情况下，我们将使用ImmutableSet.Builder，现在我们需要实现5个方法：

-   Supplier<ImmutableSet.Builder<T\>> **supplier()**
-   BiConsumer<ImmutableSet.Builder<T\>, T> **accumulator()**
-   BinaryOperator<ImmutableSet.Builder<T\>> **combiner()**
-   Function<ImmutableSet.Builder<T\>, ImmutableSet<T\>> **finisher()**
-   Set<Characteristics\> **characteristics()**

supplier()方法返回一个Supplier实例，该实例生成一个空的累加器实例。所以在这种情况下，我们可以简单地写：

```java
@Override
public Supplier<ImmutableSet.Builder<T>> supplier() {
    return ImmutableSet::builder;
}
```

accumulator()方法返回一个用于将新元素添加到现有累加器对象的函数。所以让我们只使用Builder的add方法：

```java
@Override
public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
    return ImmutableSet.Builder::add;
}
```

combiner()方法返回一个用于将两个累加器合并在一起的函数：

```java
@Override
public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
    return (left, right) -> left.addAll(right.build());
}
```

finisher()方法返回一个用于将累加器转换为最终结果类型的函数。所以在这种情况下，我们将只使用Builder的build方法：

```java
@Override
public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
    return ImmutableSet.Builder::build;
}
```

features()方法用于为Stream提供一些将用于内部优化的附加信息。在这种情况下，我们不注意Set中的元素顺序，因为我们将使用Characters.UNORDERED。要获取有关此主题的更多信息，请查看特性的JavaDoc：

```java
@Override public Set<Characteristics> characteristics() {
    return Sets.immutableEnumSet(Characteristics.UNORDERED);
}
```

这是完整的实现以及用法：

```java
public class ImmutableSetCollector<T>
      implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {

    @Override
    public Supplier<ImmutableSet.Builder<T>> supplier() {
        return ImmutableSet::builder;
    }

    @Override
    public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
        return ImmutableSet.Builder::add;
    }

    @Override
    public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
        return (left, right) -> left.addAll(right.build());
    }

    @Override
    public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
        return ImmutableSet.Builder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Sets.immutableEnumSet(Characteristics.UNORDERED);
    }

    public static <T> ImmutableSetCollector<T> toImmutableSet() {
        return new ImmutableSetCollector<>();
    }
}
```

最后，这是测试：

```java
List<String> givenList = Arrays.asList("a", "bb", "ccc", "dddd");

ImmutableSet<String> result = givenList.stream()
    .collect(toImmutableSet());
```

## 5. 总结

在本文中，我们深入探讨了Java 8的收集器，并展示了如何实现它。请务必查看我的一个[增强Java并行处理能力的项目](https://github.com/pivovarit/parallel-collectors)。