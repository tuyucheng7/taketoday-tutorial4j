## 1. 概述

Java Stream API是Java 8版本的主要特性。Streams表示延迟计算的对象序列，并提供丰富、流畅且类似monadic的API。

在本文中，我们将快速了解获取Stream的最后一个元素的方法。**请记住，由于流的性质，它不是自然操作**。始终确保你没有使用无限流。

## 2. 使用Reduce API

Reduce，简单地说，将Stream中的元素集减少为单个元素。

在这种情况下，我们将减少元素集以获取Stream中的最后一个元素。请记住，**此方法只会返回顺序流的确定性结果**。

我们使用一个字符串集合，从集合中获取流，然后减少：

```java
List<String> valueList = new ArrayList<>();
valueList.add("Joe");
valueList.add("John");
valueList.add("Sean");

Stream<String> stream = valueList.stream();
stream.reduce((first, second) -> second)
        .orElse(null);
```

在这里，流被缩减到只剩下最后一个元素的级别。如果流为空，它将返回一个空值。

## 3. 使用Skip

获取流的最后一个元素的另一种方法是跳过它之前的所有元素。这可以使用Stream类的skip函数来实现。请记住，在这种情况下，我们使用了两次流，因此会对性能产生一些明显的影响。

让我们创建一个字符串值集合，并使用其size函数来确定要跳过多少个元素才能到达最后一个元素。

下面是使用skip获取最后一个元素的示例代码：

```java
List<String> valueList = new ArrayList<String>();
valueList.add("Joe");
valueList.add("John");
valueList.add("Sean");

long count = valueList.stream().count();
Stream<String> stream = valueList.stream();
   
stream.skip(count - 1).findFirst().get();

```

“Sean”最终成为最后一个元素。

## 4. 获取无限流的最后一个元素

试图获取无限流的最后一个元素将导致对无限元素执行无限序列的评估。除非我们使用limit操作将无限流限制为特定数量的元素，否则skip和reduce都不会从评估的执行中返回。

下面是我们获取无限流并尝试获取最后一个元素的示例代码：

```java
Stream<Integer> stream = Stream.iterate(0, i -> i + 1);
stream.reduce((first, second) -> second).orElse(null);
```

因此，流不会从评估中返回，最终会停止程序的执行。

## 5. 总结

我们介绍了使用reduce和skip API获取Stream最后一个元素的不同方法，并说明了为什么这对于无限流是不可能的。

我们看到，与从其他数据结构中获取最后一个元素相比，从Stream中获取最后一个元素并不容易。这是因为Streams的惰性性质，除非调用终端函数，否则不会对其进行评估，并且我们永远不知道当前评估的元素是否是最后一个。