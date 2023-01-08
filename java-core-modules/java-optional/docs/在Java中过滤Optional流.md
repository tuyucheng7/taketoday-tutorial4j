## 1. 概述

在本文中，我们将讨论如何从Optionals流中过滤掉非空值。

我们将研究三种不同的方法——两种使用Java8，一种使用Java9 中的新支持。

我们将在所有示例中使用相同的列表：

```java
List<Optional<String>> listOfOptionals = Arrays.asList(
  Optional.empty(), Optional.of("foo"), Optional.empty(), Optional.of("bar"));
```

## 2.使用过滤器()

Java 8 中的一个选项是使用Optional::isPresent过滤掉值，然后使用Optional::get函数执行映射以提取值：

```java
List<String> filteredList = listOfOptionals.stream()
  .filter(Optional::isPresent)
  .map(Optional::get)
  .collect(Collectors.toList());
```

## 3. 使用flatMap()

另一种选择是将flatMap与 lambda 表达式一起使用，该表达式将空Optional转换为空Stream实例，并将非空Optional转换为仅包含一个元素的Stream实例：

```java
List<String> filteredList = listOfOptionals.stream()
  .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
  .collect(Collectors.toList());
```

或者，你可以使用将 Optional转换为Stream的不同方式来应用相同的方法：

```java
List<String> filteredList = listOfOptionals.stream()
  .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
  .collect(Collectors.toList());
```

## 4.Java9 的 Optional::stream

随着Java9 的到来，将stream()方法添加到Optional中，所有这些都将变得非常简单。

这种方法类似于第 3 节中展示的方法，但这次我们使用预定义的方法将Optional实例转换为Stream实例：

无论Optional值是否存在，它都将返回一个或零个元素的流：

```java
List<String> filteredList = listOfOptionals.stream()
  .flatMap(Optional::stream)
  .collect(Collectors.toList());
```

## 5.总结

有了这个，我们很快就看到了三种从 Optionals Stream中过滤当前值的方法。