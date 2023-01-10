## 1. 概述

在这篇简短的文章中，我们将研究 Apache Commons Collections 库中一个有趣的数据结构—— BidiMap。

BidiMap在标准Map接口之上增加了使用相应值查找键的可能性。

## 2.依赖关系

我们需要在我们的项目中包含以下依赖项，以便我们使用BidiMap及其实现。对于基于 Maven 的项目，我们必须将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

对于基于 Gradle 的项目，我们必须将相同的工件添加到我们的build.gradle文件中：

```groovy
compile 'org.apache.commons:commons-collections4:4.1'
```

可以[在 Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")上找到此依赖项的最新版本。

## 3. 实现和实例化

BidiMap本身只是一个接口，它定义了双向地图特有的行为——当然有多种实现可用。

重要的是要了解BidiMap 的实现不允许键和值重复。当BidiMap被反转时，任何重复的值都将被转换为重复的键，并且将违反地图契约。地图必须始终具有唯一键。

让我们看看这个接口的不同具体实现：

-   DualHashBidiMap：此实现使用两个HashMap实例在内部实现 BidiMap 。它使用条目的键或值提供条目的快速查找。但是，必须维护两个HashMap实例
-   DualLinkedHashBidiMap：此实现使用两个LinkedHashMap实例，因此维护映射条目的插入顺序。如果我们不需要维护映射条目的插入顺序，我们可以只使用更便宜的DualHashBidiMap
-   TreeBidiMap：这个实现效率很高，是用红黑树实现的。TreeBidiMap的键和值保证使用键和值的自然顺序按升序排序
-   还有DualTreeBidiMap，它使用TreeMap的两个实例来实现与TreeBidiMap相同的功能。DualTreeBidiMap明显比TreeBidiMap贵

BidiMap接口扩展了java.util.Map接口，因此可以作为它的直接替代品。我们可以使用具体实现的无参数构造函数来实例化一个具体的对象实例。

## 4.独特的BidiMap方法

现在我们已经探索了不同的实现，让我们看看接口特有的方法。

put() 将一个新的键值对插入到 map中。请注意，如果新条目的值与任何现有条目的值匹配，则现有条目将被删除以支持新条目。

该方法返回已删除的旧条目，如果没有则返回null ：

```java
BidiMap<String, String> map = new DualHashBidiMap<>();
map.put("key1", "value1");
map.put("key2", "value2");
assertEquals(map.size(), 2);
```

inverseBidiMap()反转 BidiMap的键值对。此方法返回一个新的BidiMap，其中键已成为值，反之亦然。此操作在翻译和词典应用程序中非常有用：

```java
BidiMap<String, String> rMap = map.inverseBidiMap();
assertTrue(rMap.containsKey("value1") && rMap.containsKey("value2"));
```

removeValue()用于通过指定值而不是键来删除映射条目。这是对java.util包中的Map实现的补充：

```java
map.removeValue("value2");
assertFalse(map.containsKey("key2"));
```

我们可以使用getKey()将键映射到BidiMap中的特定值。如果没有键映射到指定值，该方法返回null ：

```java
assertEquals(map.getKey("value1"), "key1");
```

## 5.总结

本快速教程介绍了 Apache Commons Collections 库——特别是BidiMap及其实现和特殊方法。

BidiMap最令人兴奋和与众不同的功能是它能够通过键和值查找和操作条目。