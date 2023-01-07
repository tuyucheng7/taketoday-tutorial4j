## 1. 概述

在这个简短的教程中，我们将了解一些不同的方法来计算ArrayList中的重复元素。

## 2. 循环使用Map.put()

我们的预期结果是一个Map对象，它包含输入列表中的所有元素作为键，每个元素的计数作为值。

实现此目的最直接的解决方案是遍历输入列表并针对每个元素：

-   如果resultMap包含该元素，我们将计数器加 1
-   否则，我们将一个新的地图条目(element, 1)放入地图

```java
public <T> Map<T, Long> countByClassicalLoop(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    for (T element : inputList) {
        if (resultMap.containsKey(element)) {
            resultMap.put(element, resultMap.get(element) + 1L);
        } else {
            resultMap.put(element, 1L);
        }
    }
    return resultMap;
}
```

此实现具有最佳兼容性，因为它适用于所有现代Java版本。

如果我们不需要Java8 之前的兼容性，我们可以进一步简化我们的方法：

```java
public <T> Map<T, Long> countByForEachLoopWithGetOrDefault(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    inputList.forEach(e -> resultMap.put(e, resultMap.getOrDefault(e, 0L) + 1L));
    return resultMap;
}
```

接下来，让我们创建一个输入列表来测试该方法：

```java
private List<String> INPUT_LIST = Lists.list(
  "expect1",
  "expect2", "expect2",
  "expect3", "expect3", "expect3",
  "expect4", "expect4", "expect4", "expect4");

```

现在让我们验证一下：

```java
private void verifyResult(Map<String, Long> resultMap) {
    assertThat(resultMap)
      .isNotEmpty().hasSize(4)
      .containsExactly(
        entry("expect1", 1L),
        entry("expect2", 2L),
        entry("expect3", 3L),
        entry("expect4", 4L));
}

```

我们将在其余方法中重复使用此测试工具。

## 3. 循环使用Map.compute()

在Java8 中，方便的[compute()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#compute(K,java.util.function.BiFunction))方法已被引入到Map接口中。我们也可以使用这个方法：

```java
public <T> Map<T, Long> countByForEachLoopWithMapCompute(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    inputList.forEach(e -> resultMap.compute(e, (k, v) -> v == null ? 1L : v + 1L));
    return resultMap;
}
```

注意(k, v) -> v == null ？1L : v + 1L是实现BiFunction<T, Long, Long>接口的重映射函数。对于给定的键，它要么返回其当前值加一(如果该键已经存在于映射中)，要么返回默认值 1。

为了使代码更具可读性，我们可以将重映射函数提取到它的变量中，甚至将其作为countByForEachLoopWithMapCompute 的输入参数。

## 4. 使用Map.merge()循环

使用Map.compute()时，我们必须显式处理空值——例如，如果给定键的映射不存在。这就是我们在重映射函数中实施空检查的原因。然而，这看起来并不漂亮。

[让我们在Map.merge()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#merge(K,V,java.util.function.BiFunction))方法的帮助下进一步清理我们的代码：

```java
public <T> Map<T, Long> countByForEachLoopWithMapMerge(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    inputList.forEach(e -> resultMap.merge(e, 1L, Long::sum));
    return resultMap;
}
```

现在代码看起来干净简洁。

让我们解释一下merge()是如何工作的。如果给定键的映射不存在，或者它的值为null，它会将键与提供的值相关联。否则，它会使用重新映射函数计算一个新值并相应地更新映射。

请注意，这次我们使用Long::sum作为BiFunction<T, Long, Long>接口实现。

## 5. 流 API Collectors.toMap()

既然我们已经谈到了Java8，我们就不能忘记强大的 Stream API。感谢 Stream API，我们可以用非常紧凑的方式解决问题。

toMap [()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Collectors.html#toMap(java.util.function.Function,java.util.function.Function,java.util.function.BinaryOperator))收集器帮助我们将输入列表转换为Map：

```java
public <T> Map<T, Long> countByStreamToMap(List<T> inputList) {
    return inputList.stream().collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));
}
```

toMap ()是一个[方便的收集器](https://www.baeldung.com/java-collectors-tomap)，它可以帮助我们将流转换为不同的Map实现。

## 6. Stream API Collectors.groupingBy()和Collectors.counting()

除了toMap()，我们的问题可以通过另外两个收集器[groupingBy()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Collectors.html#groupingBy(java.util.function.Function,java.util.stream.Collector))和[counting()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Collectors.html#counting())来解决：

```java
public <T> Map<T, Long> countByStreamGroupBy(List<T> inputList) {
    return inputList.stream().collect(Collectors.groupingBy(k -> k, Collectors.counting()));
}
```

[Java 8 Collectors](https://www.baeldung.com/java-8-collectors)的正确使用使我们的代码紧凑且易于阅读。

## 七、总结

在这篇快速文章中，我们说明了计算列表中重复元素计数的各种方法。

如果你想复习 ArrayList 本身，[可以查看参考文章](https://www.baeldung.com/java-arraylist)。