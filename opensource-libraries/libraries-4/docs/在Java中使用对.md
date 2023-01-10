## 1. 概述

在本快速教程中，我们讨论了称为Pair的非常有用的编程概念。对提供了一种处理简单的键值关联的便捷方法，当我们想从一个方法返回两个值时特别有用。

核心Java库中提供了Pair的简单实现。除此之外，某些第三方库(如 Apache Commons 和 Vavr)已在其各自的 API 中公开了此功能。

## 延伸阅读：

## [底层的JavaHashMap](https://www.baeldung.com/java-hashmap-advanced)

Hashmap 内部结构的快速实用指南

[阅读更多](https://www.baeldung.com/java-hashmap-advanced)→

## [在Java中遍历 Map](https://www.baeldung.com/java-iterate-map)

了解在Java中循环访问 Map 条目的不同方法。

[阅读更多](https://www.baeldung.com/java-iterate-map)→

## [Java——合并多个集合](https://www.baeldung.com/java-combine-multiple-collections)

在Java中组合多个集合的快速实用指南

[阅读更多](https://www.baeldung.com/java-combine-multiple-collections)→

## 2.核心Java实现

### 2.1. 双人班_

我们可以在javafx.util包中找到Pair类。此类的构造函数有两个参数，一个键及其对应的值：

```java
Pair<Integer, String> pair = new Pair<>(1, "One");
Integer key = pair.getKey();
String value = pair.getValue();

```

此示例说明了使用 Pair 概念的简单整数到字符串映射。

如图所示，通过调用getKey()方法检索pair对象中的键，而通过调用getValue()检索值。

### 2.2. AbstractMap.SimpleEntry和AbstractMap.SimpleImmutableEntry

SimpleEntry被定义为AbstractMap类中的嵌套类。要创建这种类型的对象，我们可以向构造函数提供键和值：

```java
AbstractMap.SimpleEntry<Integer, String> entry 
  = new AbstractMap.SimpleEntry<>(1, "one");
Integer key = entry.getKey();
String value = entry.getValue();
```

可以通过标准的 getter 和 setter 方法访问键和值。

此外，AbstractMap类还包含一个表示不可变对的嵌套类，即SimpleImmutableEntry类：

```java
AbstractMap.SimpleImmutableEntry<Integer, String> entry
  = new AbstractMap.SimpleImmutableEntry<>(1, "one");
```

这与可变对类的工作方式类似，除了不能更改对的值。尝试这样做将导致UnsupportedOperationException。

## 3.阿帕奇公地

在 Apache Commons 库中，我们可以在org.apache.commons.lang3.tuple包中找到Pair类。这是一个抽象类，所以不能直接实例化。

在这里我们可以找到两个代表不可变和可变对的子类，Imm utablePair和MutablePair。

两种实现都可以访问键/值 getter/setter 方法：

```java
ImmutablePair<Integer, String> pair = new ImmutablePair<>(2, "Two");
Integer key = pair.getKey();
String value = pair.getValue();
```

不出所料，尝试在ImmutablePair上调用setValue()会导致UnsupportedOperationException。

但是，该操作对于可变实现是完全有效的：

```java
Pair<Integer, String> pair = new MutablePair<>(3, "Three");
pair.setValue("New Three");

```

## 4. 瓦弗

在 Vavr 库中，配对功能由不可变的Tuple2类提供：

```java
Tuple2<Integer, String> pair = new Tuple2<>(4, "Four");
Integer key = pair._1();
String value = pair._2();

```

在此实现中，我们无法在创建后修改对象，因此变异方法返回一个包含所提供更改的新实例：

```java
tuplePair = pair.update2("New Four");

```

## 5. 备选方案 I – 简单容器类

根据用户偏好或在没有任何上述库的情况下，配对功能的标准解决方法是创建一个简单的容器类来包装所需的返回值。

这里最大的优势是能够提供我们的名字，这有助于避免让相同的类代表不同的域对象：

```java
public class CustomPair {
    private String key;
    private String value;

    // standard getters and setters
}
```

## 6.备选方案 II – 数组

另一种常见的解决方法是使用包含两个元素的简单数组来实现类似的结果：

```java
private Object[] getPair() {
    // ...
    return new Object[] {key, value};
}
```

通常，键位于数组的索引 0 处，而其对应的值位于索引 1 处。

## 七. 总结

在本文中，我们讨论了Java中Pairs的概念以及核心Java和其他第三方库中可用的不同实现。