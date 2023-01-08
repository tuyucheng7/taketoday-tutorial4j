## 1. 概述

在这个简短的教程中，我们将了解如何将相等的对象分组并计算它们在Java中的出现次数。我们将在Java中使用[groupingBy()收集器。](https://www.baeldung.com/java-groupingby-collector)

## 2. 使用Collectors.groupingBy()计算出现次数

Collectors.groupingBy()提供类似于 SQL 中的 GROUP BY 子句的功能。我们可以使用它按任何属性对对象进行分组并将结果存储在Map中。

例如，让我们考虑一个场景，我们需要在流中对相等的String进行分组并计算它们的出现次数：

```java
List<String> list = new ArrayList<>(Arrays.asList("Foo", "Bar", "Bar", "Bar", "Foo"));
```

我们可以将相等的字符串分组，在本例中为“Foo”和“Bar”。结果Map会将这些字符串存储为键。这些键的值将是出现次数。“Foo”的值为 2，“Bar”的值为 3：

```java
Map<String, Long> result = list.stream()
  .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
Assert.assertEquals(new Long(2), result.get("Foo"));
Assert.assertEquals(new Long(3), result.get("Bar"));
```

让我们解码上面的代码片段：

-   Map<String, Long> 结果——这是输出结果Map，它将分组元素存储为键并将它们的出现次数计为值
-   list.stream() –我们将列表元素转换为[Java 流](https://www.baeldung.com/java-8-streams)以声明方式处理集合
-   Collectors.groupingBy() – 这是Collectors类的方法，用于按某些属性对对象进行分组并将结果存储在 Map实例中
-   Function.identity() –它是Java中的[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)；identity 方法返回一个始终返回其输入参数的函数
-   Collectors.counting() – 此Collectors类方法计算流中传递的元素数量作为参数

我们可以使用Collectors.groupingByConcurrent()而不是Collectors.groupingBy()。它还对输入流元素执行分组操作。该方法将结果收集到ConcurrentMap中，从而提高效率。

例如，对于输入列表：

```java
List<String> list = new ArrayList<>(Arrays.asList("Adam", "Bill", "Jack", "Joe", "Ian"));
```

我们可以使用Collectors.groupingByConcurrent()对等长字符串进行分组：

```java
Map<Integer, Long> result = list.stream()
  .collect(Collectors.groupingByConcurrent(String::length, Collectors.counting()));
Assert.assertEquals(new Long(2), result.get(3));
Assert.assertEquals(new Long(3), result.get(4));
```

## 3.总结

在本文中，我们介绍了Collector.groupingBy()对相等对象进行分组的用法。