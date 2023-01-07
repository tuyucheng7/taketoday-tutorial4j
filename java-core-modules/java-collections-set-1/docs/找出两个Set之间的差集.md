## 1. 概述

[Set](https://www.baeldung.com/java-hashset)是Java中常用的集合类型之一。今天，我们将讨论如何找出两个给定集合之间的差异。

## 二、问题简介

在我们仔细研究实现之前，我们首先需要了解问题所在。像往常一样，一个例子可以帮助我们快速理解需求。

假设我们有两个Set对象，set1和set2：

```java
set1: {"Kotlin", "Java", "Rust", "Python", "C++"}
set2: {"Kotlin", "Java", "Rust", "Ruby", "C#"}
```

正如我们所见，这两个集合都包含一些编程语言名称。“找出两个集合之间的差异”这一要求可能有两种变体：

-   非对称差异——找出那些被set1包含但不被set2包含的元素；在这种情况下，预期结果是{“Python”, “C++”}
-   [Symmetric difference](https://en.wikipedia.org/wiki/Symmetric_difference) – 在两个集合中找到元素，但不在它们的交集中；如果我们查看示例，结果应该是{“Python”、“C++”、“Ruby”、“C#”}

在本教程中，我们将解决这两种情况的解决方案。首先，我们将专注于寻找不对称差异。之后，我们将探索找到两组之间的对称差异。

接下来，让我们看看它们的实际效果。

## 3. 不对称差异

### 3.1. 使用标准的removeAll方法

Set类提供了[removeAll](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html#removeAll(java.util.Collection))方法。该方法实现了Collection接口的removeAll方法。

removeAll方法接受一个Collection对象作为参数，并从给定的Set对象中移除该参数中的所有元素。因此，如果我们以这种方式“ set1.removeAll(set2) ”将set2对象作为参数传递，则set1对象中的其余元素将成为结果。

为简单起见，让我们将其显示为单元测试：

```java
Set<String> set1 = Stream.of("Kotlin", "Java", "Rust", "Python", "C++").collect(Collectors.toSet());
Set<String> set2 = Stream.of("Kotlin", "Java", "Rust", "Ruby", "C#").collect(Collectors.toSet());
Set<String> expectedOnlyInSet1 = Set.of("Python", "C++");

set1.removeAll(set2);

assertThat(set1).isEqualTo(expectedOnlyInSet1);
```

如上面的方法所示，首先，我们使用Stream[初始化两个Set](https://www.baeldung.com/java-initialize-hashset)对象。然后，在调用removeAll方法后， set 1 对象包含预期的元素。

这种方法非常简单。但是缺点也很明显：去掉set1中的公共元素后，修改 了原来的 set1。

因此，如果我们在调用removeAll方法后仍然需要它，则需要备份原始set1对象，或者如果 set1是[不可变](https://www.baeldung.com/java-immutable-set)[Set](https://www.baeldung.com/java-immutable-set) ，则必须创建一个新的可变 set 对象。

接下来，让我们看看另一种在不修改原始集合的情况下在新Set对象中返回非对称差异的方法。

### 3.2. 使用Stream.filter方法

[Stream API](https://www.baeldung.com/java-streams)从Java8 开始就存在了。它允许我们使用 Stream.filter 方法从集合中过滤[元素](https://www.baeldung.com/java-stream-filter-lambda)。

我们也可以在不修改原始set1对象的情况下使用Stream.filter来解决这个问题。让我们首先将这两个集合初始化为不可变集合：

```java
Set<String> immutableSet1 = Set.of("Kotlin", "Java", "Rust", "Python", "C++");
Set<String> immutableSet2 = Set.of("Kotlin", "Java", "Rust", "Ruby", "C#");
Set<String> expectedOnlyInSet1 = Set.of("Python", "C++");
```

从Java9 开始，Set接口引入了 static [of](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html#of(E...))方法。它允许我们方便地初始化一个不可变的Set对象。也就是说，如果我们尝试修改immutableSet1，则会抛出UnsupportedOperationException。

接下来，让我们编写一个使用Stream.filter来查找差异的单元测试：

```java
Set<String> actualOnlyInSet1 = immutableSet1.stream().filter(e -> !immutableSet2.contains(e)).collect(Collectors.toSet());
assertThat(actualOnlyInSet1).isEqualTo(expectedOnlyInSet1);

```

正如我们在上面的方法中看到的，关键是“ filter(e -> !immutableSet2.contains(e)) ”。在这里，我们只获取immutableSet1中但不在immutableSet2中的元素。

如果我们执行这个测试方法，它会毫无例外地通过。这意味着这种方法有效，并且原始集没有被修改。

### 3.3. 使用 Guava 库

[Guava](https://www.baeldung.com/guava-guide)是一个流行的Java库，它附带了一些新的集合类型和方便的辅助方法。Guava 提供了一种方法来找到两个集合之间的不对称差异。因此，我们可以使用这种方法轻松解决我们的问题。

但首先，我们需要将该库包含在我们的类路径中。假设我们通过[Maven](https://www.baeldung.com/maven)管理项目依赖项。我们可能需要将[Guava 依赖](https://search.maven.org/search?q=g:com.google.guava AND a:guava)项添加到pom.xml中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>

```

一旦 Guava 在我们的Java项目中可用，我们就可以使用它的[Sets.difference](https://guava.dev/releases/31.0-jre/api/docs/com/google/common/collect/Sets.html#difference(java.util.Set,java.util.Set))方法来获得预期的结果：

```java
Set<String> actualOnlyInSet1 = Sets.difference(immutableSet1, immutableSet2);
assertThat(actualOnlyInSet1).isEqualTo(expectedOnlyInSet1);

```

值得一提的是，Sets.difference方法返回一个包含结果的不可变Set视图。 它的意思是：

-   我们不能修改返回的集合
-   如果原始集是可变的，则对原始集的更改可能会反映在我们的结果集视图中

### 3.4. 使用 Apache 公共库

Apache Commons 是另一个广泛使用的库。[Apache Commons Collections4](https://www.baeldung.com/apache-commons-collection-utils)库提供了许多很好的与集合相关的方法作为对标准集合 API 的补充。

在我们开始使用它之前，让我们将依赖项添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>

```

同样，我们可以在 Maven 的中央仓库找到[最新版本。](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-collections4)

commons-collections4库有一个CollectionUtils.removeAll方法。它类似于标准的Collection.removeAll方法，但在新的Collection 对象中返回结果， 而不是修改第一个Collection对象。

接下来，让我们用两个不可变的 Set对象来测试它：

```java
Set<String> actualOnlyInSet1 = new HashSet<>(CollectionUtils.removeAll(immutableSet1, immutableSet2));
assertThat(actualOnlyInSet1).isEqualTo(expectedOnlyInSet1);

```

如果我们执行它，测试将通过。但是，我们应该注意CollectionUtils.removeAll方法返回Collection类型的结果。

如果需要具体类型——例如，在我们的例子中是Set——我们需要手动转换它。在上面的测试方法中，我们使用返回的集合初始化了一个新的HashSet对象。

## 4.对称差异

到目前为止，我们已经学习了如何获得两个集合之间的不对称差异。现在，让我们仔细看看另一种情况：找到两个集合之间的对称差异。

我们将介绍两种方法来从我们的两个不可变集示例中获得对称差异。

预期结果是：

```java
Set<String> expectedDiff = Set.of("Python", "C++", "Ruby", "C#");
```

接下来，让我们看看如何解决这个问题。

### 4.1. 使用HashMap

解决该问题的一个想法是首先创建一个Map<T, Integer>对象。

然后，我们遍历两个给定的集合，并将每个元素作为键放入映射中。如果键存在于映射中，则意味着这是两个集合中的公共元素。我们将一个特殊数字设置为值——例如Integer.MAX_VALUE。否则，我们将元素和值 1 作为映射中的新条目。

最后，我们在映射中找出值为 1 的键，这些键是两个给定集合之间的对称差异。

接下来，让我们用Java实现这个想法：

```java
public static <T> Set<T> findSymmetricDiff(Set<T> set1, Set<T> set2) {
    Map<T, Integer> map = new HashMap<>();
    set1.forEach(e -> putKey(map, e));
    set2.forEach(e -> putKey(map, e));
    return map.entrySet().stream()
      .filter(e -> e.getValue() == 1)
      .map(Map.Entry::getKey)
      .collect(Collectors.toSet());
}

private static <T> void putKey(Map<T, Integer> map, T key) {
    if (map.containsKey(key)) {
        map.replace(key, Integer.MAX_VALUE);
    } else {
        map.put(key, 1);
    }
}

```

现在，让我们测试一下我们的解决方案，看看它是否能给出预期的结果：

```java
Set<String> actualDiff = SetDiff.findSymmetricDiff(immutableSet1, immutableSet2);
assertThat(actualDiff).isEqualTo(expectedDiff);

```

如果我们运行它，测试就会通过。也就是说，我们的实现按预期工作。

### 4.2. 使用 Apache 公共库

在查找两个集合之间的不对称差异时，我们已经介绍了 Apache Commons 库。实际上，commons -collections4库有一个方便的SetUtils.disjunction方法可以直接返回两个集合之间的对称差异：

```java
Set<String> actualDiff = SetUtils.disjunction(immutableSet1, immutableSet2);
assertThat(actualDiff).isEqualTo(expectedDiff);

```

如上面的方法所示，与CollectionUtils.removeAll方法不同，SetUtils.disjunction方法返回一个Set对象。我们不需要手动将其转换为Set。

## 5.总结

在本文中，我们探讨了如何通过示例找出两个Set对象之间的差异。此外，我们已经讨论了这个问题的两个变体：找到不对称差异和对称差异。

我们已经解决了使用标准JavaAPI 和广泛使用的外部库(例如 Apache Commons-Collections 和 Guava)解决这两个变体的问题。