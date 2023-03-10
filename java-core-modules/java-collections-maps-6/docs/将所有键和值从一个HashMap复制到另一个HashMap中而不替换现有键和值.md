## 一、简介

在本教程中，我们将了解如何将一个*HashMap*复制到另一个 HashMap 而无需替换目标*HashMap 的键和值。*Java中的HashMap是*Map*接口的哈希表实现，是*[一种](https://www.baeldung.com/java-hashmap)* 支持存储键值对的数据结构。

## 2.问题陈述

考虑我们有两个 *HashMap* s *，* *sourceMap*和*targetMap，* 包含国家和他们的首都作为键和值*。**我们想将sourceMap*的内容复制到*targetMap*中，这样我们只有一张包含所有国家及其首都的地图。复制应遵守以下规则：

-   *我们应该保留targetMap*的原始内容
-   如果键发生冲突，例如两个地图中都存在的城市，我们应该保留 targetMap 的*条目*

让我们接受以下输入：

```java
Map<String, String> sourceMap = new HashMap<>();
sourceMap.put("India", "Delhi");
sourceMap.put("United States", "Washington D.C.");
sourceMap.put("United Kingdom", "London D.C.");

Map<String, String> targetMap = new HashMap<>();
targetMap.put("Zimbabwe", "Harare");
targetMap.put("Norway", "Oslo");
targetMap.put("United Kingdom", "London");复制
```

修改后的*targetMap保留其值并添加**sourceMap* 的所有值：

```java
"India", "Delhi"
"United States", "Washington D.C."
"United Kingdom", "London"
"Zimbabwe", "Harare"
"Norway", "Oslo"复制
```

## 3. 遍历*HashMap*

解决我们问题的一种简单方法是[遍历](https://www.baeldung.com/java-iterate-map)*sourceMap*的每个条目（键值对）并将其与*targetMap 的条目进行比较。* *当我们找到只存在于sourceMap*中的条目时，我们将它添加到*targetMap 中。* 生成的*targetMap*包含其自身和 sourceMap 的所有键值*。*

我们可以遍历 *sourceMap*的 *entrySet()并检查**targetMap*中是否存在键，而不是遍历两个映射的*entrySets()*：

```java
Map<String, String> copyByIteration(Map<String, String> sourceMap, Map<String, String> targetMap) {
    for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
        if (!targetMap.containsKey(entry.getKey())) {
            targetMap.put(entry.getKey(), entry.getValue());
        }
    }
    return targetMap;
}复制
```

## 4. 使用*Map*的 *putIfAbsent()* 

我们可以重构上面的代码，使用Java 8 中新增的*putIfAbsent()*方法。该方法顾名思义， 只有当指定条目中的键不存在时，才会将*sourceMap的一个条目复制到**targetMap*中：

```java
Map<String, String> copyUsingPutIfAbsent(Map<String, String> sourceMap, Map<String, String> targetMap) {
    for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
        targetMap.putIfAbsent(entry.getKey(), entry.getValue());
    }
    return targetMap;
}复制
```

使用循环的另一种方法是使用Java 8 中添加的*[forEach](https://www.baeldung.com/foreach-java)* *结构。我们提供了一个操作，在我们的例子中是在targetMap*输入上调用 putIfAbsent *()*方法，它对给定*HashMap*的每个条目执行直到所有元素都已处理或引发异常：

```java
Map<String, String> copyUsingPutIfAbsentForEach(Map<String, String> sourceMap, Map<String, String> targetMap) {
    sourceMap.forEach(targetMap::putIfAbsent);
    return targetMap;
}复制
```

## 5. 使用*Map*的 *putAll()*

Map接口提供了一个方法*putAll()* *，*我们可以使用它来实现我们想要的结果。该方法将输入映射的所有键和值复制到当前映射中。**在这里我们应该注意，如果源哈希映射和目标哈希映射之间发生键冲突，则来自源的条目将替换*****targetMap\*****的条目**。

*我们可以通过从sourceMap*中显式删除公共键来解决这个问题：

```java
Map<String, String> copyUsingPutAll(Map<String, String> sourceMap, Map<String, String> targetMap) {
    sourceMap.keySet().removeAll(targetMap.keySet());
    targetMap.putAll(sourceMap);
    return targetMap;
}复制
```

## 6.在*地图*上使用*merge()*

Java 8在*Maps接口中引入了**merge()*方法。**它需要一个键、一个值和一个作为方法参数的重新映射**。

假设我们在输入中指定的键尚未与当前映射中的值关联（或与*null关联）。*在这种情况下，该方法将它与提供的非*空*值相关联。

**如果键在两个映射中都存在，则关联的值将替换为给定重新映射函数的结果。**如果重新映射函数的结果为*null*，它会删除键值对。

我们可以使用*merge()方法将条目从**sourceMap*复制到*targetMap*：

```java
Map<String, String> copyUsingMapMerge(Map<String, String> sourceMap, Map<String, String> targetMap) {
    sourceMap.forEach((key, value) -> targetMap.merge(key, value, (oldVal, newVal) -> oldVal));
    return targetMap;
}复制
```

 我们的重映射函数确保它在发生碰撞时保留*targetMap*中的值。

## 7. 使用 Guava 的*Maps.difference()*

Guava库在其*Maps类中*[使用](https://www.baeldung.com/guava-guide)*difference()* 方法。要使用[*Guava*](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")*库，我们应该在我们的pom.xml*中添加相应的依赖项：

```java
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>复制
```

**difference \*()\*方法将两个映射作为输入并计算这两个映射之间的差异。提供的映射的键应该遵守\*equals()\*和\*hashCode()\* [契约](https://www.baeldung.com/java-equals-hashcode-contracts)。**

为了解决我们的问题，我们首先评估地图之间的差异。*一旦我们知道仅存在于sourceMap*（左侧地图）中的条目，我们将它们放入我们的*targetMap*中：

```java
Map<String, String> copyUsingGuavaMapDifference(Map<String, String> sourceMap, Map<String, String> targetMap) {
    MapDifference<String, String> differenceMap = Maps.difference(sourceMap, targetMap);
    targetMap.putAll(differenceMap.entriesOnlyOnLeft());
    return targetMap;
}复制
```

## 八、结论

在本文中，我们研究了将条目从一个*HashMap*复制到另一个 HashMap 的不同方法，同时保留目标 HashMap 的现有条目*。* 我们实施了一种基于迭代的方法，并使用不同的 Java 库函数解决了这个问题。我们还研究了如何使用 Guava 库解决问题。