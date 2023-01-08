## 1. 概述

拆分字符串是一个非常频繁的操作；本快速教程重点介绍一些我们可以用来在Java中简单地执行此操作的 API。

## 2.字符串拆分()

让我们从核心库开始——String类本身提供了一个split()方法——这对于大多数场景来说非常方便和足够。它只是根据分隔符拆分给定的字符串，返回一个字符串数组。

让我们看一些例子。我们将从逗号分隔开始：

```java
String[] splitted = "peter,james,thomas".split(",");
```

让我们用空格分开：

```java
String[] splitted = "car jeep scooter".split(" ");
```

我们也用点分开：

```java
String[] splitted = "192.168.1.178".split(".")
```

现在让我们通过正则表达式拆分多个字符——逗号、空格和连字符：

```java
String[] splitted = "b a, e, l.d u, n g".split("s+|,s|.s"));
```

## 3. StringUtils.split() 函数

Apache 的通用 lang 包提供了一个StringUtils类——它包含一个 null 安全的split()方法，它使用空格作为默认分隔符进行分割：

```java
String[] splitted = StringUtils.split("car jeep scooter");
```

此外，它会忽略额外的空格：

```java
String[] splitted = StringUtils.split("car   jeep  scooter");
```

## 4.拆分器.split()

最后，Guava 中还有一个不错的Splitter fluent API：

```java
List<String> resultList = Splitter.on(',')
  .trimResults()
  .omitEmptyStrings()
  .splitToList("car,jeep,, scooter");

```

## 5.拆分和修剪

有时给定的字符串在定界符周围包含一些前导、尾随或额外的空格。让我们看看我们如何一次性处理拆分输入和修剪结果。

假设我们有这个作为输入：

```java
String input = " car , jeep, scooter ";
```

要删除分隔符之前和/或之后的多余空格，我们可以使用正则表达式执行拆分和修剪：

```java
String[] splitted = input.trim().split("s,s");
```

在这里， trim()方法删除输入字符串中的前导和尾随空格，正则表达式本身处理定界符周围的额外空格。

我们可以通过使用Java8 Stream功能实现相同的结果：

```java
String[] splitted = Arrays.stream(input.split(","))
  .map(String::trim)
  .toArray(String[]::new);
```

## 六，总结

String.split()通常就足够了。然而，对于更复杂的情况，我们可以使用 Apache 的基于 commons-lang 的StringUtils类，或者干净灵活的 Guava API。