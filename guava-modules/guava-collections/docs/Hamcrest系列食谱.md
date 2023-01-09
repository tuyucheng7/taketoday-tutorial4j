## 1. 简介

这本食谱说明了如何使用 Hamcrest 匹配器来处理和测试集合。

食谱的格式以示例为重点且实用——无需多余的细节和解释。

首先，让我们快速静态导入以涵盖我们接下来要使用的大部分实用程序 API：

```java
import static org.hamcrest.Matchers.;
```

## 延伸阅读：

## [Hamcrest 通用核心匹配器](https://www.baeldung.com/hamcrest-core-matchers)

探索 Hamcrest 库中 CoreMatchers 类的不同方法。

[阅读更多](https://www.baeldung.com/hamcrest-core-matchers)→

## [Hamcrest Bean 匹配器](https://www.baeldung.com/hamcrest-bean-matchers)

了解 Hamcrest bean 匹配器——一种提供有效断言方法的工具，这是编写单元测试时经常使用的功能。

[阅读更多](https://www.baeldung.com/hamcrest-bean-matchers)→

## [使用 Hamcrest 进行测试](https://www.baeldung.com/java-junit-hamcrest-guide)

在这个非常实用的教程中，我们专注于使用 Hamcrest API 以及为我们的软件编写更简洁、更直观的单元测试。

[阅读更多](https://www.baeldung.com/java-junit-hamcrest-guide)→

## 2.食谱

检查单个元素是否在集合中

```java
List<String> collection = Lists.newArrayList("ab", "cd", "ef");
assertThat(collection, hasItem("cd"));
assertThat(collection, not(hasItem("zz")));
```

检查集合中是否有多个元素

```java
List<String> collection = Lists.newArrayList("ab", "cd", "ef");
assertThat(collection, hasItems("cd", "ef"));
```

检查集合中的所有元素


– 严格的秩序

```java
List<String> collection = Lists.newArrayList("ab", "cd", "ef");
assertThat(collection, contains("ab", "cd", "ef"));
```

– 任何订单

```java
List<String> collection = Lists.newArrayList("ab", "cd", "ef");
assertThat(collection, containsInAnyOrder("cd", "ab", "ef"));
```

检查集合是否为空

```java
List<String> collection = Lists.newArrayList();
assertThat(collection, empty());
```

检查数组是否为空

```java
String[] array = new String[] { "ab" };
assertThat(array, not(emptyArray()));
```

检查地图是否为空

```java
Map<String, String> collection = Maps.newHashMap();
assertThat(collection, equalTo(Collections.EMPTY_MAP));
```

检查 Iterable 是否为空

```java
Iterable<String> collection = Lists.newArrayList();
assertThat(collection, emptyIterable());
```

检查集合的大小

```java
List<String> collection = Lists.newArrayList("ab", "cd", "ef");
assertThat(collection, hasSize(3));
```

检查可迭代对象的大小

```java
Iterable<String> collection = Lists.newArrayList("ab", "cd", "ef");
assertThat(collection, Matchers.<String> iterableWithSize(3));
```

检查每件物品的状况

```java
List<Integer> collection = Lists.newArrayList(15, 20, 25, 30);
assertThat(collection, everyItem(greaterThan(10)));
```

## 3.总结

这种格式是一个实验——我正在发布一些关于给定主题的内部开发指南[——Google Guava](https://www.baeldung.com/guava-collections)和现在的 Hamcrest。目标是让这些信息在网上随时可用——并在我遇到新的有用示例时添加到其中。