## 1. 概述

在本教程中，我们将讨论Java 8 中引入 的Map接口的新默认方法computeIfAbsent 。

具体来说，我们将查看它的签名、用法以及它如何处理不同的情况。

## 2. Map.computeIfAbsent 方法

让我们从查看computeIfAbsent的签名开始：

```java
default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
```

computeIfAbsent方法有 两个参数。第一个参数是key，第二个参数是mappingFunction。重要的是要知道只有在映射不存在时才会调用映射函数。

### 2.1. 与非空值相关的键

首先，它检查键是否存在于映射中。如果键存在，并且非空值与键相关，则返回该值：

```java
Map<String, Integer> stringLength = new HashMap<>();
stringLength.put("John", 5);
assertEquals((long)stringLength.computeIfAbsent("John", s -> s.length()), 5);
```

正如我们所见，键“John”存在一个非空映射，它返回值 5。如果使用我们的映射函数，我们希望该函数返回长度 4。

### 2.2. 使用映射函数计算值

此外，如果键不存在于映射中，或者空值与键相关，则它会尝试使用给定的mappingFunction计算值。除非计算值为空，否则它还会将计算值输入到地图中。

我们看一下computeIfAbsent方法中mappingFunction的使用 ：

```java
Map<String, Integer> stringLength = new HashMap<>();
assertEquals((long)stringLength.computeIfAbsent("John", s -> s.length()), 4);
assertEquals((long)stringLength.get("John"), 4);
```

由于键“John”不存在，它通过将键作为参数传递给mappingFunction来计算值。

### 2.3. 映射函数返回null

此外，如果mappingFunction返回null，则地图不记录任何映射：

```java
Map<String, Integer> stringLength = new HashMap<>();
assertEquals(stringLength.computeIfAbsent("John", s -> null), null);
assertNull(stringLength.get("John"));
```

### 2.4. 映射函数抛出异常

最后，如果mappingFunction抛出一个未经检查的异常，则异常被重新抛出，地图记录没有映射：

```java
@Test(expected = RuntimeException.class)
public void whenMappingFunctionThrowsException_thenExceptionIsRethrown() {
    Map<String, Integer> stringLength = new HashMap<>();
    stringLength.computeIfAbsent("John", s -> { throw new RuntimeException(); });
}
```

我们可以看到mappingFunction抛出一个RuntimeException，它传播回computeIfAbsent方法。

## 3.总结

在这篇简短的文章中，我们重点介绍了computeIfAbsent方法、它的签名和用法。最后，我们了解了它如何处理不同的情况。