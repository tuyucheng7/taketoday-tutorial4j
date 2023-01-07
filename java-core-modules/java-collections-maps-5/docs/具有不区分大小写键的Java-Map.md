## 1. 概述

Map是Java中最常见的数据结构之一，而String是映射键最常见的类型之一。默认情况下，此类映射具有区分大小写的键。

在这个简短的教程中，我们将探索不同的Map实现，这些实现接受String 的所有大小写变体作为相同的 key。

## 2. 使用不区分大小写的键仔细查看地图

让我们更详细地研究一下我们试图解决的问题。

假设我们有一个带有一个条目的Map<String, Integer> ：

[![第一次进入](https://www.baeldung.com/wp-content/uploads/2020/04/firstEntry-300x57.png)](https://www.baeldung.com/wp-content/uploads/2020/04/firstEntry.png)

让我们添加下一个条目：

```java
map.put("ABC", 2);
```

使用带有区分大小写键的Map时，我们最终会得到两个条目：

[![敏感键-1](https://www.baeldung.com/wp-content/uploads/2020/04/sensetiveKey-1-300x82.png)](https://www.baeldung.com/wp-content/uploads/2020/04/sensetiveKey-1.png)

但是当使用带有不区分大小写键的Map时，内容将是：

[![不敏感的Key-1](https://www.baeldung.com/wp-content/uploads/2020/04/insensetiveKey-1-300x57.png)](https://www.baeldung.com/wp-content/uploads/2020/04/insensetiveKey-1.png)

在接下来的示例中，我们将深入研究一些流行的Map实现的不区分大小写的实现： TreeMap、HashMap和LinkedHashMap。

## 3.树图

[TreeMap](https://www.baeldung.com/java-treemap) 是NavigableMap的一个实现，这意味着它总是在插入后根据给定的Comparator对条目进行排序。此外， TreeMap使用Comparator来查找插入的键是重复键还是新键。

因此，如果我们提供一个不区分大小写的String Comparator，我们将得到一个不区分大小写的TreeMap。

幸运的是，String已经提供了这个静态Comparator：

```java
public static final Comparator <String> CASE_INSENSITIVE_ORDER
```

我们可以在构造函数中提供：

```java
Map<String, Integer> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
treeMap.put("abc", 1);
treeMap.put("ABC", 2);
```

现在，当我们运行测试时，我们可以看到Map 的大小是 1：

```java
assertEquals(1, treeMap.size());
```

并将值更新为 2：

```java
assertEquals(2, treeMap.get("aBc").intValue());
assertEquals(2, treeMap.get("ABc").intValue());
```

现在让我们使用相同的String删除条目，但使用另一种情况：

```java
treeMap.remove("aBC");
assertEquals(0, treeMap.size());
```

我们应该记住，与提供 O(1) 插入和查找的[HashMap](https://www.baeldung.com/java-treemap-vs-hashmap)[相比，](https://www.baeldung.com/java-treemap-vs-hashmap)[TreeMap的](https://www.baeldung.com/java-treemap-vs-hashmap)put和get等函数平均花费 O(log n)的时间。

还值得注意的是，TreeMap 不允许空键。

## 4.Apache的CaseInsensitiveMap

[Apache 的 Commons-Collections](https://github.com/apache/commons-collections)是一个非常流行的Java库，提供了大量有用的类，其中就有[CaseInsensitiveMap 。](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/map/CaseInsensitiveMap.html)

CaseInsensitiveMap 是一个[基于散列的Map](https://www.baeldung.com/java-hashmap)，它在添加或检索键之前将键转换为小写。 与TreeMap不同， CaseInsensitiveMap 允许空 键插入。

[首先，](https://search.maven.org/search?q=g:org.apache.commons a:commons-collections4)我们需要添加[commons-collections4](https://search.maven.org/search?q=g:org.apache.commons a:commons-collections4)[依赖](https://search.maven.org/search?q=g:org.apache.commons a:commons-collections4)：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

现在，我们可以使用CaseInsensitiveMap 并添加两个条目：

```java
Map<String, Integer> commonsHashMap = new CaseInsensitiveMap<>();
commonsHashMap.put("abc", 1);
commonsHashMap.put("ABC", 2);
```

当我们测试它时，我们期望得到与之前看到的相同的结果：

```java
assertEquals(1, commonsHashMap.size());
assertEquals(2, commonsHashMap.get("aBc").intValue());
assertEquals(2, commonsHashMap.get("ABc").intValue());

commonsHashMap.remove("aBC");
assertEquals(0, commonsHashMap.size());
```

## 5. Spring的LinkedCaseInsensitiveMap

[Spring Core](https://github.com/spring-projects/spring-framework/tree/master/spring-core)是一个 Spring Framework 模块，它还提供实用程序类，包括LinkedCaseInsensitiveMap。

LinkedCaseInsensitiveMap包装了一个[LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)，它是一个基于哈希表和链表的Map 。与LinkedHashMap不同，它不允许空键插入。LinkedCaseInsensitiveMap保留原始顺序以及键的原始大小写，同时允许在任何情况下调用get和remove等函数。

首先，让我们添加[spring -core依赖](https://search.maven.org/search?q=g:org.springframework a:spring-core)：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.2.5.RELEASE</version>
</dependency>
```

现在，我们可以初始化一个新的LinkedCaseInsensitiveMap：

```java
Map<String, Integer> linkedHashMap = new LinkedCaseInsensitiveMap<>();
linkedHashMap.put("abc", 1);
linkedHashMap.put("ABC", 2);
```

添加测试它：

```java
assertEquals(1, linkedHashMap.size());
assertEquals(2, linkedHashMap.get("aBc").intValue());
assertEquals(2, linkedHashMap.get("ABc").intValue());

linkedHashMap.remove("aBC");
assertEquals(0, linkedHashMap.size());
```

## 六，总结

在本教程中，我们研究了使用不区分大小写的键创建JavaMap的不同方法，并使用了不同的类来实现这一点。