## 1. 概述

在本快速教程中，我们将探讨在Java中组合集合的不同方法。

我们将探索使用Java和外部框架(如 Guava、Apache 等)的各种方法。有关集合的介绍，请[在此处查看本系列](https://www.baeldung.com/java-collections)。

## 2. 使用馆藏的外部图书馆 

除了本机方法，我们还将使用外部库。请在pom.xml中添加以下依赖项 ：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.2</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-exec</artifactId>
    <version>1.3</version>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在 Maven Central 上找到[Commons](https://search.maven.org/classic/#search|ga|1|a%3A"commons-collections4")、[Commons-exec](https://search.maven.org/classic/#search|ga|1|a%3A"commons-exec")和[Guava](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")的最新版本。

## 3. 在Java中组合数组

### 3.1. 本机Java解决方案

Java 带有一个内置的[void arraycopy()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#arraycopy(java.lang.Object,int,java.lang.Object,int,int)) 方法，可以将给定的源数组到目标数组。

我们可以通过以下方式使用它：

```java
Object[] combined = new Object[first.length + second.length];
System.arraycopy(first, 0, combined, 0, first.length);
System.arraycopy(second, 0, combined, first.length, second.length);
```

在这个方法中，除了数组对象，我们还指定了我们需要的位置，我们还传递了长度参数。

这是本机Java解决方案，因此不需要任何外部库。

### 3.2. 使用Java8流API

Streams 提供了一种有效的方法来迭代几种不同类型的集合。要开始使用流，请转到 [Java 8 Stream API 教程](https://www.baeldung.com/java-8-streams)。

要使用Stream组合数组 ，我们可以使用以下代码：

```java
Object[] combined = Stream.concat(Arrays.stream(first), Arrays.stream(second)).toArray();
```

[Stream.concat()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#concat(java.util.stream.Stream,java.util.stream.Stream)) 创建一个连接流，其中第一个流的元素后面是第二个流的元素，然后使用toArray() 方法将其转换为数组。

创建流的过程在不同类型的集合中是相同的。然而，我们可以用不同的方式收集它，从中检索不同的数据结构。

我们将在 4.2 节中重新讨论此方法。和 5.2。看看我们如何在Lists和Sets上使用相同的方法。

### 3.3. 使用来自 Apache Commons 的ArrayUtils

Apache 公共库为我们提供了 [ArrayUtils包中的](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/ArrayUtils.html)[addAll()](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/ArrayUtils.html#addAll-T:A-T...-) 方法 。我们可以提供目标数组和源数组作为参数，此方法将返回一个组合数组：

```java
Object[] combined = ArrayUtils.addAll(first, second);
```

[Array Processing with Apache Commons Lang 3](https://www.baeldung.com/array-processing-commons-lang)一文中也详细讨论了此方法 。

### 3.4. 使用番石榴

出于同样的目的，Guava 为我们提供了 [concat()](https://google.github.io/guava/releases/19.0/api/docs/com/google/common/collect/ObjectArrays.html#concat(T[], T[], java.lang.Class))方法：

```java
Object [] combined = ObjectArrays.concat(first, second, Object.class);
```

它可以与不同的数据类型一起使用，并且它接受两个源数组以及类文字以返回组合数组。

## 4. Java中的组合列表

### 4.1. 使用Collection Native addAll()方法

[Collection](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html)接口本身为我们提供了 [addAll()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html#addAll(java.util.Collection)) 方法，它将指定集合中的所有元素添加到调用者对象中。[这篇 Baeldung 文章](https://www.baeldung.com/java-combine-multiple-collections)也对此进行了详细讨论：

```java
List<Object> combined = new ArrayList<>();
combined.addAll(first);
combined.addAll(second);
```

由于该方法是在Collections框架的最父接口 Collection 接口中提供的，因此可以跨所有List和Set应用。

### 4.2. 使用Java 8

我们可以通过以下方式使用Stream和Collectors来组合Lists：

```java
List<Object> combined = Stream.concat(first.stream(), second.stream()).collect(Collectors.toList());
```

这与我们在 3.2 节中对数组所做的相同，但我们没有将其转换为数组，而是使用收集器将其转换为列表。要详细了解收集器，请访问 [Java 8 收集器指南](https://www.baeldung.com/java-8-collectors)。

我们也可以这样使用 flatMaps：

```java
List<Object> combined = Stream.of(first, second).flatMap(Collection::stream).collect(Collectors.toList());
```

首先，我们使用[Stream.of()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#of(T...))返回两个列表的顺序流 -  first和second。然后我们将它传递给flatMap，它将在应用映射函数后返回映射流的内容。此方法也在 [Java 中的合并流一](https://www.baeldung.com/java-merge-streams) 文中进行了讨论。

要了解有关 flatMap的更多信息，请转至[这篇 Baeldung 文章](https://www.baeldung.com/java-difference-map-and-flatmap)。

### 4.3. 使用来自 Apache Commons 的ListUtils

CollectionUtils.union 合并两个集合并返回一个包含所有元素的集合：

```java
List<Object> combined = ListUtils.union(first, second);
```

[Apache Commons Collections CollectionUtils](https://www.baeldung.com/apache-commons-collection-utils)[指南](https://www.baeldung.com/apache-commons-collection-utils)中也讨论了此方法 。有关详细信息，请转到第 4.9 节。这篇文章的。

### 4.4. 使用番石榴

要使用 Guava 合并 List，我们将使用由concat()方法组成的 Iterable 。连接所有集合后，我们可以快速得到组合的List对象，如本例所示：

```java
Iterable<Object> combinedIterables = Iterables
  .unmodifiableIterable(Iterables.concat(first, second));
List<Object> combined = Lists.newArrayList(combinedIterables);
```

## 5.在Java中组合集合

### 5.1. 普通Java解决方案

正如我们在 4.1 节中已经讨论过的，[Collection](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html)接口带有一个内置的 [addAll() ](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/ArrayUtils.html#addAll-T:A-T...-)方法，该方法也可用于 Lists和 Sets：

```java
Set<Object> combined = new HashSet<>();
combined.addAll(first);
combined.addAll(second);
```

### 5.2. 使用Java8 流

可以在此处应用我们用于List 对象的相同函数：

```java
Set<Object> combined = Stream
  .concat(first.stream(), second.stream())
  .collect(Collectors.toSet());
```

与 list 相比，这里唯一显着的区别是我们没有使用[Collectors.toList()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Collectors.html#toList())，而是使用[Collectors.toSet()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Collectors.html#toSet())将提供的两个流中的所有元素累积到一个新的 Set中。

与Lists类似，在Sets上使用flatMaps 时，它看起来像：

```java
Set<Object> combined = Stream.of(first, second)
  .flatMap(Collection::stream)
  .collect(Collectors.toSet());
```

### 5.3. 使用 Apache Commons

与 ListUtils类似，我们也可以使用 SetUtils来执行Set元素的并集：

```java
Set<Object> combined = SetUtils.union(first, second);
```

### 5.4. 从番石榴中使用

Guava 库为我们提供了直接的Sets.union()方法来组合Java中的集合：

```java
Set<Object> combined = Sets.union(first, second);
```

## 6. Java中结合Map

### 6.1. 普通Java解决方案

我们可以利用 Map接口，它本身为我们提供了 putAll()方法，该方法将所有映射从 Map 对象的提供参数到调用方Map对象：

```java
Map<Object, Object> combined = new HashMap<>();
combined.putAll(first);
combined.putAll(second);
```

### 6.2. 使用Java 8

从Java8 开始，Map类由 接受键、值和 [BiFunction的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/BiFunction.html)merge()方法组成。我们可以将其与Java8 [forEach](https://www.baeldung.com/foreach-java)语句一起使用来实现合并功能：

```java
second.forEach((key, value) -> first.merge(key, value, String::concat));
```

第三个参数，即重映射函数在两个源映射中存在相同的键值对时很有用。此函数指定应如何处理这些类型的值。

我们也可以像这样使用flatMap：

```java
Map<String, String> combined = Stream.of(first, second)
  .map(Map::entrySet)
  .flatMap(Collection::stream)
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, String::concat));
```

### 6.3. 使用 Apache Commons Exec

[Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)为我们提供了一个简单的[merge(Map first, Map second)](https://commons.apache.org/proper/commons-exec/apidocs/org/apache/commons/exec/util/MapUtils.html#merge(java.util.Map, java.util.Map)) 方法：

```java
Map<String, String> combined = MapUtils.merge(first, second);
```

### 6.4. 使用谷歌番石榴

我们可以使用 Google 的 Guava 库提供的[ImmutableMap 。](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/collect/ImmutableMap.html) 它的[putAll()](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/collect/ImmutableMap.Builder.html#putAll-java.util.Map-)方法将所有给定地图的键和值关联到构建的地图中：

```java
Map<String, String> combined = ImmutableMap.<String, String>builder()
  .putAll(first)
  .putAll(second)
  .build();
```

## 七、总结

在本文中，我们通过不同的方法来组合不同类型的Collections。我们合并了arrays、 Lists、 Sets和 Maps。