## 1. 概述

在本教程中，我们将展示在Java中实现单词计数器的各种方法。

## 2. 计数器实现

让我们从简单地计算这个数组中单词的字数开始：

```java
static String[] COUNTRY_NAMES 
    = { "China", "Australia", "India", "USA", "USSR", "UK", "China", 
    "France", "Poland", "Austria", "India", "USA", "Egypt", "China" };
```

如果我们想处理大文件，我们需要选择[此处](https://www.baeldung.com/java-read-lines-large-file)描述的其他选项。

### 2.1 整数Map

最简单的解决方案之一是创建一个Map，将单词存储为键，将出现次数存储为值：

```java
Map<String, Integer> counterMap = new HashMap<>();

for (String country : COUNTRY_NAMES) { 
    counterMap.compute(country, (k, v) -> v == null ? 1 : v + 1); 
}

assertEquals(3, counterMap.get("China").intValue());
assertEquals(2, counterMap.get("India").intValue());
```

我们只是使用了Map方便的计算方法，该方法会增加计数器或在密钥不存在时将其初始化为1。

然而，这种创建计数器的方法效率不高，因为Integer是不可变的，所以每次当我们增加计数器时，我们都会创建一个新的Integer对象。

### 2.2 流API

现在，让我们利用Java 8 Stream API、并行Streams和[groupingBy()](https://www.baeldung.com/java-groupingby-collector)收集器：

```java
@Test
public void whenMapWithLambdaAndWrapperCounter_runsSuccessfully() {
    Map<String, Long> counterMap = new HashMap<>();
 
    Stream.of(COUNTRY_NAMES)
        .collect(Collectors.groupingBy(k -> k, ()-> counterMap, Collectors.counting());

    assertEquals(3, counterMap.get("China").intValue());
    assertEquals(2, counterMap.get("India").intValue());
}
```

同样，我们可以使用parallelStream：

```java
@Test
public void whenMapWithLambdaAndWrapperCounter_runsSuccessfully() {
    Map<String, Long> counterMap = new HashMap<>();
 
    Stream.of(COUNTRY_NAMES).parallel()
        .collect(Collectors.groupingBy(k -> k, ()-> counterMap, Collectors.counting());

    assertEquals(3, counterMap.get("China").intValue());
    assertEquals(2, counterMap.get("India").intValue());
}
```

### 2.3 使用整数数组Map

接下来，让我们使用一个Map将计数器包装在用作值的Integer数组中：

```java
@Test
public void whenMapWithPrimitiveArrayCounter_runsSuccessfully() {
    Map<String, int[]> counterMap = new HashMap<>();

    counterWithPrimitiveArray(counterMap);

    assertEquals(3, counterMap.get("China")[0]);
    assertEquals(2, counterMap.get("India")[0]);
}
 
private void counterWithPrimitiveArray(Map<String, int[]> counterMap) {
    for (String country : COUNTRY_NAMES) {
        counterMap.compute(country, (k, v) -> v == null ? new int[] { 0 } : v)[0]++;
    }
}
```

请注意我们是如何创建一个简单的HashMap并将int数组作为值的。

在counterWithPrimitiveArray方法中，在迭代数组的每个值时，我们：

-   通过将国家名称作为键传递来调用counterMap上的get
-   检查密钥是否已经存在。如果该条目已经存在，我们将创建一个带有单个“1”的原始整数数组的新实例。如果条目不存在，我们增加数组中存在的计数器值

这种方法比包装器实现更好-因为它创建的对象更少。

### 2.4 使用MutableInteger Map

接下来，让我们创建一个包装器对象，它嵌入了一个原始整数计数器，如下所示：

```java
private static class MutableInteger {
    int count = 1;
	
    public void increment() {
        this.count++;
    }
	
    // getter and setter
}
```

让我们看看如何使用上面的类作为计数器：

```java
@Test
public void whenMapWithMutableIntegerCounter_runsSuccessfully() {
    Map<String, MutableInteger> counterMap = new HashMap<>();

    mapWithMutableInteger(counterMap);

    assertEquals(3, counterMap.get("China").getCount());
    assertEquals(2, counterMap.get("India").getCount());
}
private void counterWithMutableInteger(
  Map<String, MutableInteger> counterMap) {
    for (String country : COUNTRY_NAMES) {
        counterMap.compute(country, (k, v) -> v == null ? new MutableInteger(0) : v).increment();
    }
}
```

在mapWithMutableInteger方法中，在遍历COUNTRY_NAMES数组中的每个国家时，我们：

-   通过将国家名称作为键传递来调用counterMap上的get
-   检查密钥是否已经存在。如果没有条目，我们创建一个MutableInteger实例，它将计数器值设置为1。如果地图中存在该国家/地区，我们会增加MutableInteger中存在的计数器值

这种创建计数器的方法比以前的方法要好-因为我们重用了相同的MutableInteger，从而创建了更少的对象。

这就是Apache Collections HashMultiSet的工作方式，它在内部嵌入一个值为MutableInteger的HashMap。

## 3. 性能分析

这是比较上面列出的每种方法的性能的图表。

<img src="../assets/img_1.png">

上面的图表是使用JMH创建的，下面是创建上面统计数据的代码：

```java
Map<String, Integer> counterMap = new HashMap<>();
Map<String, MutableInteger> counterMutableIntMap = new HashMap<>();
Map<String, int[]> counterWithIntArrayMap = new HashMap<>();
Map<String, Long> counterWithLongWrapperMap = new HashMap<>();
 
@Benchmark
public void wrapperAsCounter() {
    counterWithWrapperObject(counterMap);
}

@Benchmark
public void lambdaExpressionWithWrapper() {
    counterWithLambdaAndWrapper(counterWithLongWrapperMap );
}

@Benchmark
public void parallelStreamWithWrapper() {
    counterWithParallelStreamAndWrapper(counterWithLongWrapperStreamMap);
}
    
@Benchmark
public void mutableIntegerAsCounter() {
    counterWithMutableInteger(counterMutableIntMap);
}
    
@Benchmark
public void mapWithPrimitiveArray() {
   counterWithPrimitiveArray(counterWithIntArrayMap);
}
```

## 4. 总结

在这篇简短的文章中，我们说明了使用Java创建单词计数器的各种方法。