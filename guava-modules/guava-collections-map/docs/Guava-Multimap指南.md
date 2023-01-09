## 1. 概述

在本文中，我们将研究 Google Guava 库中的一种Map实现——Multimap。它是一个将键映射到值的集合，类似于java.util.Map，但其中每个键可能与多个值相关联。

## 2.Maven依赖

首先，让我们添加一个依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")找到。

## 3.多图实现

在 Guava Multimap 的情况下，如果我们为同一个键添加两个值，第二个值将不会覆盖第一个值。相反，我们将在生成的map中有两个值。我们来看一个测试用例：

```java
String key = "a-key";
Multimap<String, String> map = ArrayListMultimap.create();

map.put(key, "firstValue");
map.put(key, "secondValue");

assertEquals(2, map.size());

```

打印map的内容将输出：

```javascript
{a-key=[firstValue, secondValue]}
```

当我们通过键“a-key”获取值时，我们将得到包含“firstValue”和“secondValue”的Collection<String>作为结果：

```java
Collection<String> values = map.get(key);
```

打印值将输出：

```javascript
[firstValue, secondValue]
```

## 4. 与标准地图相比

java.util包中的标准映射不让我们能够将多个值分配给同一个键。让我们考虑一个简单的情况，当我们使用相同的键将两个值放入Map中时：

```java
String key = "a-key";
Map<String, String> map = new LinkedHashMap<>();

map.put(key, "firstValue");
map.put(key, "secondValue");

assertEquals(1, map.size());

```

生成的映射只有一个元素(“secondValue”)，因为第二个put()操作覆盖了第一个值。如果我们想要实现与 Guava 的Multimap相同的行为，我们需要创建一个具有List<String>作为值类型的Map ：

```java
String key = "a-key";
Map<String, List<String>> map = new LinkedHashMap<>();

List<String> values = map.get(key);
if(values == null) {
    values = new LinkedList<>();
    values.add("firstValue");
    values.add("secondValue");
 }

map.put(key, values);

assertEquals(1, map.size());
```

显然，使用起来不是很方便。如果我们的代码中有这样的需求，那么 Guava 的Multimap可能是比java.util.Map 更好的选择。

这里要注意的一件事是，虽然我们有一个包含两个元素的列表，但size()方法返回 1。在Multimap 中，size()返回存储在Map 中的实际值数，但keySet().size ()返回不同键的数量。

## 5. Multimap的优点

Multimap 通常用于Map<K, Collection<V>>本来会出现的地方。差异包括：

-   在使用put()添加条目之前无需填充空集合
-   get() 方法从不返回null，只返回一个空集合(我们不需要像Map<String, Collection<V>>测试用例中那样检查null )
-   当且仅当它映射到至少一个值时，一个键才包含在Multimap中。任何导致键具有零关联值的操作都具有从Multimap中删除该键的效果(在Map<String, Collection<V>> 中，即使我们从集合中删除所有值，我们仍然保留一个空的集合作为一个值，这是不必要的内存开销)
-   总条目值计数可用作size()

## 六. 总结

本文介绍如何以及何时使用 Guava Multimap。它将它与标准的java.util.Map进行了比较，并展示了 Guava Multimap 的优点。