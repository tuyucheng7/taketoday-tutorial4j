## 1. 概述

在本快速教程中，我们将演示三种不同的方法来从映射中检索给定值的键。 我们还将讨论各种解决方案的优缺点。

要了解有关Map接口的更多信息，你可以查看[这篇文章](https://www.baeldung.com/java-hashmap)。

## 2.迭代方法

Java Collections的Map接口提供了一个名为entrySet()的方法。它返回Set中映射的所有条目或键值对。

这个想法是迭代这个条目集并返回值与提供的值匹配的键：

```java
public <K, V> K getKey(Map<K, V> map, V value) {
    for (Entry<K, V> entry : map.entrySet()) {
        if (entry.getValue().equals(value)) {
            return entry.getKey();
        }
    }
    return null;
}
```

但是，可能存在多个键指向同一个值的可能性。

在这种情况下，如果找到匹配值，我们将键添加到Set并继续循环。最后，我们返回包含所有所需键的Set ：

```java
public <K, V> Set<K> getKeys(Map<K, V> map, V value) {
    Set<K> keys = new HashSet<>();
    for (Entry<K, V> entry : map.entrySet()) {
        if (entry.getValue().equals(value)) {
            keys.add(entry.getKey());
        }
    }
    return keys;
}
```

虽然这是一个非常直接的实现，但即使在几次迭代后找到所有匹配项，它也会比较所有条目。

## 3. 函数式方法

随着Java8 中 Lambda 表达式的引入，我们可以用更灵活和可读的方式来做。我们将条目集转换为Stream并提供 lambda 以仅过滤具有给定值的那些条目。

然后我们使用 map 方法从过滤的条目中返回键的Stream ：

```java
public <K, V> Stream<K> keys(Map<K, V> map, V value) {
    return map
      .entrySet()
      .stream()
      .filter(entry -> value.equals(entry.getValue()))
      .map(Map.Entry::getKey);
}
```

返回流的优点是它可以满足广泛的客户端需求。调用代码可能只需要一个键或所有指向所提供值的键。由于流的评估是惰性的，客户端可以根据需要控制迭代次数。

此外，客户端可以使用适当的收集器将流转换为任何集合：

```java
Stream<String> keyStream1 = keys(capitalCountryMap, "South Africa");
String capital = keyStream1.findFirst().get();

Stream<String> keyStream2 = keys(capitalCountryMap, "South Africa");
Set<String> capitals = keyStream2.collect(Collectors.toSet());
```

## 4. 使用 Apache Commons 集合

如果我们需要为特定地图非常频繁地调用函数，上述想法将不会很有帮助。它会不必要地一次又一次地迭代它的键集。

在这种情况下，维护另一个值到键的映射会更有意义，因为它需要恒定的时间来检索值的键。

Apache的Commons Collections库提供了这样一个称为[BidiMap](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/BidiMap.html)的双向Map。它有一个名为getKey()的方法 ，用于检索给定值的键：

```java
BidiMap<String, String> capitalCountryMap = new DualHashBidiMap<>();
capitalCountryMap.put("Berlin", "Germany");
capitalCountryMap.put("Cape Town", "South Africa");
String capitalOfGermany = capitalCountryMap.getKey("Germany");
```

但是，BidiMap在其键和值之间强加了 1:1 的关系。如果我们尝试将值已存在的键值对放入映射中，它会删除旧条目。换句话说，它根据值更新键。

此外，它需要大量内存来保存反向映射。

有关如何使用BidiMap的更多详细信息，请参见[本教程](https://www.baeldung.com/commons-collections-bidi-map)。

## 5.使用谷歌番石榴

我们可以使用谷歌开发的 Guava 中的另一个双向Map ，称为[BiMap](https://google.github.io/guava/releases/19.0/api/docs/com/google/common/collect/BiMap.html) 。该类提供了一个名为inverse()的方法来获取 value-key Map或 reverse Map以根据给定的值获取键：

```java
HashBiMap<String, String> capitalCountryMap = HashBiMap.create();
capitalCountryMap.put("Berlin", "Germany");
capitalCountryMap.put("Cape Town", "South Africa");
String capitalOfGermany = capitalCountryMap.inverse().get("Germany");
```

与BidiMap一样，BiMap也不允许多个键引用相同的值。如果我们尝试进行 这样的尝试，它 会抛出 java.lang.IllegalArgumentException。

不用说，BiMap还使用大量内存，因为它必须在内部存储逆映射。如果你有兴趣了解更多关于BiMap的知识，你可以看看[这个教程](https://www.baeldung.com/guava-bimap)。 

## 六，总结

在这篇简短的文章中，我们讨论了一些在给定值的情况下检索Map键的方法。每种方法都有其优点和缺点。我们应该始终考虑用例并根据情况选择最合适的用例。