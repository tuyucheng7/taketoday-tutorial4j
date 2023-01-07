## 1. 概述

在本教程中，我们将了解Java 中Map和MultivaluedMap之间的区别。但在此之前，让我们先看一些例子。

## 2.地图示例

HashMap实现了Map接口，它也允许空值和空键：

```java
@Test
public void givenHashMap_whenEquals_thenTrue() {
    Map<String, Integer> map = new HashMap<>();

    // Putting key-value pairs into our map.
    map.put("first", 1);
    map.put(null, 2);
    map.put("third", null);

    // The assert statements. The last arguments is what's printed if the assertion fails.
    assertNotNull(map, "The HashMap is null!");
    assertEquals(1, map.get("first"), "The key isn't mapped to the right value!");
    assertEquals(2, map.get(null), "HashMap didn't accept null as key!");
    assertEquals(null, map.get("third"), "HashMap didn't accept null value!");
}
```

以上单元测试成功通过。正如我们所见，每个键都映射到一个值。

## 3.如何将MultivaluedMap添加到我们的项目中？

在使用MultivaluedMap接口及其实现类之前，我们需要将其库[Jakarta RESTful WS API](https://search.maven.org/artifact/jakarta.ws.rs/jakarta.ws.rs-api)添加到我们的 Maven 项目中。为此，我们需要在项目的pom.xml文件中声明一个依赖项：

```xml
<dependency>
    <groupId>jakarta.ws.rs</groupId>
    <artifactId>jakarta.ws.rs-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

我们已经成功地将库添加到我们的项目中。

## 4. MultivaluedMap示例

MultivaluedHashMap实现了MultivaluedMap接口，它允许空值和 空 键：

```java
@Test
public void givenMultivaluedHashMap_whenEquals_thenTrue() {
    MultivaluedMap<String, Integer> mulmap = new MultivaluedHashMap<>();

    // Mapping keys to values.
    mulmap.addAll("first", 1, 2, 3);
    mulmap.add(null, null);

    // The assert statements. The last argument is what's printed if the assertion fails.
    assertNotNull(mulmap, "The MultivaluedHashMap is null!");
    assertEquals(1, mulmap.getFirst("first"), "The key isn't mapped to the right values!");
    assertEquals(null, mulmap.getFirst(null), "MultivaluedHashMap didn't accept null!");
}
```

以上单元测试成功通过。在这里，每个键都映射到可以包含零个或多个元素的值列表。

## 5.有什么区别？

在Java生态中，[Map](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/Map.html)和[MultivaluedMap](https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/MultivaluedMap.html)都是接口。不同之处在于，在Map中，每个键都映射到一个对象。然而，在MultivaluedMap中，我们可以有零个或多个对象与同一个键相关联。

此外，MultivaluedMap是Map 的子接口，因此它拥有自己的所有方法和一些自己的方法。一些实现Map的类是[HashMap](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/HashMap.html)、[LinkedHashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/LinkedHashMap.html)、[ConcurrentHashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)、[WeakHashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/WeakHashMap.html)、[EnumMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html)和[TreeMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TreeMap.html)。此外，[MultivaluedHashMap](https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/MultivaluedHashMap.html)是一个同时实现Map和MultivaluedMap的类。

例如， addFirst(K key, V value) 是MultivaluedMap的方法之一，它将值添加到所提供键的当前值列表的第一个位置：

```java
MultivaluedMap<String, String> mulmap = new MultivaluedHashMap<>();
mulmap.addFirst("firstKey", "firstValue");
```

另一方面，getFirst(K key)获取提供的键的第一个值：

```java
String value = mulmap.getFirst("firstKey");
```

最后， addAll(K key, V ... newValues)将多个值添加到提供的键的当前值列表中：

```java
mulmap.addAll("firstKey", "secondValue", "thirdValue");
```

## 6.总结

在本文中，我们看到了Map和MultivaluedMap之间的区别。