## 1. 概述

在本快速教程中，我们将了解在 Java中循环访问Map条目的不同方法。

简单地说，我们可以使用entrySet()、keySet()或values()提取Map的内容。由于这些都是集合，因此类似的迭代原则适用于所有这些集合。

让我们仔细看看其中的一些。

## 延伸阅读：

## [Java 8 forEach 指南](https://www.baeldung.com/foreach-java)

Java 8 forEach 快速实用指南

[阅读更多](https://www.baeldung.com/foreach-java)→

## [如何使用索引迭代流](https://www.baeldung.com/java-stream-indices)

了解使用索引迭代Java8 Streams 的几种方法

[阅读更多](https://www.baeldung.com/java-stream-indices)→

## [查找Java映射中的最大值](https://www.baeldung.com/java-find-map-max)

查看在JavaMap 结构中查找最大值的方法。

[阅读更多](https://www.baeldung.com/java-find-map-max)→

## 2. Map的entrySet()、keySet()和values()方法的简短介绍

在我们使用这三种方法遍历地图之前，让我们先了解一下这些方法的作用：

-   [entrySet()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#entrySet()) – 返回地图的集合视图，其元素来自Map.Entry类。entry.getKey ()方法返回key， entry.getValue()返回对应的value
-   [keySet()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#keySet()) – 返回此映射中包含的所有键作为集合
-   [values()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#values()) – 返回此映射中包含的所有值作为集合

接下来，让我们看看这些方法的实际应用。

## 3.使用for循环

### 3.1. 使用entrySet()

首先，让我们看看如何使用Entry Set遍历Map：

```java
public void iterateUsingEntrySet(Map<String, Integer> map) {
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
        System.out.println(entry.getKey() + ":" + entry.getValue());
    }
}
```

在这里，我们 从Map中提取条目集， 然后使用经典的 for-each 方法遍历它们。

### 3.2. 使用键集()

或者，我们可以先使用keySet方法获取Map中的所有键，然后按每个键遍历映射：

```java
public void iterateUsingKeySetAndForeach(Map<String, Integer> map) {
    for (String key : map.keySet()) {
        System.out.println(key + ":" + map.get(key));
    }
}
```

### 3.3. 使用values()迭代值

有时，我们只对映射中的值感兴趣，而不管与它们相关联的键是什么。在这种情况下，values()是我们的最佳选择：

```java
public void iterateValues(Map<String, Integer> map) {
    for (Integer value : map.values()) {
        System.out.println(value);
    }
}

```

## 4.迭代器 

执行迭代的另一种方法是使用[迭代器](https://www.baeldung.com/java-iterator)。接下来，让我们看看这些方法如何与Iterator对象一起使用。

### 4.1. 迭代器和entrySet()

首先，让我们使用 Iterator 和entrySet()遍历地图：

```java
public void iterateUsingIteratorAndEntry(Map<String, Integer> map) {
    Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
        Map.Entry<String, Integer> entry = iterator.next();
        System.out.println(entry.getKey() + ":" + entry.getValue());
    }
}
```

请注意我们如何使用entrySet()返回的Set的iterator() API获取Iterator实例。然后，像往常一样，我们使用iterator.next() 遍历迭代器。

### 4.2. 迭代器和keySet()

同样，我们可以使用Iterator和keySet()遍历Map：

```java
public void iterateUsingIteratorAndKeySet(Map<String, Integer> map) {
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
        String key = iterator.next();
        System.out.println(key + ":" + map.get(key));
    }
}

```

### 4.3. 迭代器和值()

我们还可以使用Iterator和values()方法遍历地图的值：

```java
public void iterateUsingIteratorAndValues(Map<String, Integer> map) {
    Iterator<Integer> iterator = map.values().iterator();
    while (iterator.hasNext()) {
        Integer value = iterator.next();
        System.out.println("value :" + value);
    }
}
```

## 5. 使用 Lambdas 和 Stream API

从版本 8 开始，Java 引入了[Stream API](https://www.baeldung.com/java-8-streams)和 lambdas。接下来，让我们看看如何使用这些技术迭代地图。

### 5.1. 使用forEach()和 Lambda

与Java8 中的大多数其他内容一样，事实证明这比替代方案简单得多。我们将只使用forEach()方法：

```java
public void iterateUsingLambda(Map<String, Integer> map) {
    map.forEach((k, v) -> System.out.println((k + ":" + v)));
}

```

在这种情况下，我们不需要将映射转换为一组条目。要了解有关 lambda 表达式的更多信息，我们可以[从这里开始](https://www.baeldung.com/java-8-lambda-expressions-tips)。

当然，我们可以从键开始遍历地图：

```java
public void iterateByKeysUsingLambda(Map<String, Integer> map) {
    map.keySet().foreach(k -> System.out.println((k + ":" + map.get(k))));
}

```

同样，我们可以对values()方法使用相同的技术：

```java
public void iterateValuesUsingLambda(Map<String, Integer> map) {
    map.values().forEach(v -> System.out.println(("value: " + v)));
}

```

### 5.2. 使用流API

Stream API 是Java8 的一个重要特性。我们也可以使用此特性来循环遍历Map。

当我们计划进行一些额外的Stream处理时，应该使用Stream API；否则，它只是一个简单的forEach()，如前所述。

下面以entrySet()为例，看看Stream API 是如何工作的：

```java
public void iterateUsingStreamAPI(Map<String, Integer> map) {
    map.entrySet().stream()
      // ... some other Stream processings
      .forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
}

```

[Stream API](https://www.baeldung.com/java-8-streams)与keySet()和values()方法的用法与上面的示例非常相似。

## 六，总结

在本文中，我们专注于一个关键但直接的操作：遍历Map的条目。

我们探索了几种只能用于Java8+ 的方法，即 Lambda 表达式和Stream API。