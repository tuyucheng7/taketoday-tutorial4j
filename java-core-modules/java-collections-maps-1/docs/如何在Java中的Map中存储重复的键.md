## 1. 概述

在本教程中，我们将探讨处理具有重复键的Map的可用选项，或者换句话说，允许为单个键存储多个值的Map 。

## 2. 标准地图

Java 有几种Map接口的实现，每一种都有自己的特殊性。

但是，现有的Java核心 Map 实现都不允许Map处理单个键的多个值。

如我们所见，如果我们尝试为同一个键插入两个值，第二个值将被存储，而第一个值将被删除。

它还将返回(通过[put(K key, V value)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html#put(K,V))方法的每个正确实现)：

```java
Map<String, String> map = new HashMap<>();
assertThat(map.put("key1", "value1")).isEqualTo(null);
assertThat(map.put("key1", "value2")).isEqualTo("value1");
assertThat(map.get("key1")).isEqualTo("value2");

```

那么，我们如何才能实现所需的行为呢？

## 3.收藏价值

显然，为Map的每个值使用一个Collection就可以完成这项工作：

```java
Map<String, List<String>> map = new HashMap<>();
List<String> list = new ArrayList<>();
map.put("key1", list);
map.get("key1").add("value1");
map.get("key1").add("value2");
 
assertThat(map.get("key1").get(0)).isEqualTo("value1");
assertThat(map.get("key1").get(1)).isEqualTo("value2");

```

然而，这种冗长的解决方案有多个缺点并且容易出错。这意味着我们需要为每个值实例化一个Collection，在添加或删除值之前检查它是否存在，在没有值时手动删除它，等等。

从Java8 开始，我们可以利用compute()方法并改进它：

```java
Map<String, List<String>> map = new HashMap<>();
map.computeIfAbsent("key1", k -> new ArrayList<>()).add("value1");
map.computeIfAbsent("key1", k -> new ArrayList<>()).add("value2");

assertThat(map.get("key1").get(0)).isEqualTo("value1");
assertThat(map.get("key1").get(1)).isEqualTo("value2");

```

但是，我们应该避免它，除非有充分的理由不这样做，例如限制性公司政策阻止我们使用第三方库。

否则，在编写我们自己的自定义Map实现并重新发明轮子之前，我们应该在开箱即用的几个选项中进行选择。

## 4.Apache Commons 集合

像往常一样，Apache为我们的问题提供了解决方案。

让我们从导入最新版本的Common Collections(从现在开始是 CC)开始：

```xml
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-collections4</artifactId>
  <version>4.1</version>
</dependency>
```

### 4.1. 多图

[org.apache.commons.collections4](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/MultiMap.html)。[MultiMap](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/MultiMap.html)接口定义了一个 Map，它包含针对每个键的值集合。

它由[org.apache.commons.collections4.map 实现。MultiValueMap](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/map/MultiValueMap.html)类，它自动处理引擎盖下的大部分样板文件：

```java
MultiMap<String, String> map = new MultiValueMap<>();
map.put("key1", "value1");
map.put("key1", "value2");
assertThat((Collection<String>) map.get("key1"))
  .contains("value1", "value2");

```

虽然此类自 CC 3.2 起可用，但它不是线程安全的，并且在 CC 4.1 中已被弃用。我们应该只在无法升级到较新版本时才使用它。

### 4.2. 多值地图

MultiMap的继承者是[org.apache.commons.collections4。多值映射](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/MultiValuedMap.html)接口。它有多种实现可供使用。

让我们看看如何将我们的多个值存储到一个ArrayList中，它保留了重复项：

```java
MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
map.put("key1", "value1");
map.put("key1", "value2");
map.put("key1", "value2");
assertThat((Collection<String>) map.get("key1"))
  .containsExactly("value1", "value2", "value2");

```

或者，我们可以使用HashSet，它会删除重复项：

```java
MultiValuedMap<String, String> map = new HashSetValuedHashMap<>();
map.put("key1", "value1");
map.put("key1", "value1");
assertThat((Collection<String>) map.get("key1"))
  .containsExactly("value1");

```

以上两种实现都不是线程安全的。

让我们看看如何使用UnmodifiableMultiValuedMap装饰器使它们不可变：

```java
@Test(expected = UnsupportedOperationException.class)
public void givenUnmodifiableMultiValuedMap_whenInserting_thenThrowingException() {
    MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
    map.put("key1", "value1");
    map.put("key1", "value2");
    MultiValuedMap<String, String> immutableMap =
      MultiMapUtils.unmodifiableMultiValuedMap(map);
    immutableMap.put("key1", "value3");
}

```

## 5.番石榴多图

Guava 是用于JavaAPI 的 Google 核心库。

让我们从在我们的项目中导入 Guava 开始：

```xml
<dependency>
  <groupId>com.google.guava</groupId>
  <artifactId>guava</artifactId>
  <version>31.0.1-jre</version>
</dependency>
```

Guava 从一开始就遵循了多种实现的路径。

最常见的是[com.google.common.collect。ArrayListMultimap](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/ArrayListMultimap.html)，它为每个值使用由ArrayList支持的HashMap ：

```java
Multimap<String, String> map = ArrayListMultimap.create();
map.put("key1", "value2");
map.put("key1", "value1");
assertThat((Collection<String>) map.get("key1"))
  .containsExactly("value2", "value1");

```

一如既往，我们应该更喜欢 Multimap 接口的不可变实现：[com.google.common.collect。ImmutableListMultimap](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/ImmutableListMultimap.html)和[com.google.common.collect。不可变集多图](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/ImmutableSetMultimap.html)。

### 5.1. 常见的地图实现

当我们需要一个特定的Map实现时，首先要做的是检查它是否存在，因为 Guava 可能已经实现了它。

例如，我们可以使用[com.google.common.collect。LinkedHashMultimap](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/LinkedHashMultimap.html)，保留键和值的插入顺序：

```java
Multimap<String, String> map = LinkedHashMultimap.create();
map.put("key1", "value3");
map.put("key1", "value1");
map.put("key1", "value2");
assertThat((Collection<String>) map.get("key1"))
  .containsExactly("value3", "value1", "value2");

```

或者，我们可以使用[com.google.common.collect。TreeMultimap](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/TreeMultimap.html)，它按自然顺序迭代键和值：

```java
Multimap<String, String> map = TreeMultimap.create();
map.put("key1", "value3");
map.put("key1", "value1");
map.put("key1", "value2");
assertThat((Collection<String>) map.get("key1"))
  .containsExactly("value1", "value2", "value3");

```

### 5.2. 打造我们的自定义MultiMap

许多其他的实现是可用的。

但是，我们可能想要装饰尚未实现的Map和/或List 。

幸运的是，Guava 有一个工厂方法允许我们这样做[——Multimap.newMultimap()](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/Multimaps.html#newMultimap-java.util.Map-com.google.common.base.Supplier-)。

## 六，总结

我们已经了解了如何以所有主要的现有方式在Map中存储一个键的多个值。

我们已经探索了 Apache Commons Collections 和 Guava 最流行的实现，如果可能的话，它们应该比自定义解决方案更受欢迎。