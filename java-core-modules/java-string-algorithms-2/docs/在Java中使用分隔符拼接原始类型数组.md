## 1. 概述

在本快速教程中，我们将学习如何在Java中使用单字符分隔符连接基元数组。对于我们的示例，我们将考虑两个数组：一个int 数组和一个char数组。

## 2. 定义问题

让我们首先为示例定义一个 int 数组和一个 char数组 ，以及我们将用来连接它们的内容的分隔符：

```java
int[] intArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
char[] charArray = {'a', 'b', 'c', 'd', 'e', 'f'};
char separatorChar = '-';
String separator = String.valueOf(separatorChar);

```

请注意，我们同时包含了char和String分隔符，因为我们将展示的一些方法需要一个char参数，而其他方法需要一个String argument。

连接操作的结果将包含int数组的“1-2-3-4-5-6-7-8-9”和char数组的“abcdef”。

## 3.收集器.joining()

让我们从Java8 Stream API 中的一种可用方法开始 — Collectors.joining()。

首先，我们使用java.util包中的Arrays.stream() 方法 从基元数组 创建一个Stream 。接下来，我们将每个元素映射到String。最后，我们将元素与给定的分隔符连接起来。

让我们从我们的 int数组开始：

```java
String joined = Arrays.stream(intArray)
  .mapToObj(String::valueOf)
  .collect(Collectors.joining(separator));
```

当用这个方法加入我们的 char数组时，我们必须先将char数组 包装 到CharBuffer中，然后再投影到char中。这是因为 chars()方法返回一个 Stream的int 值。

不幸的是，Java Stream API 没有提供用于包装 char Stream的本机方法。

让我们加入我们的char数组：

```java
String joined = CharBuffer.wrap(charArray).chars()
  .mapToObj(intValue -> String.valueOf((char) intValue))
  .collect(Collectors.joining(separator));
```

## 4.字符串连接器

与Collectors.joining()类似，此方法使用 Stream API，但它不是收集元素，而是遍历元素并将它们添加到 StringJoiner实例：

```java
StringJoiner intStringJoiner = new StringJoiner(separator);
Arrays.stream(intArray)
  .mapToObj(String::valueOf)
  .forEach(intStringJoiner::add);
String joined = intStringJoiner.toString();
```

同样，在使用 Stream API 时，我们必须将 char数组包装到CharBuffer中：

```java
StringJoiner charStringJoiner = new StringJoiner(separator);
CharBuffer.wrap(charArray).chars()
  .mapToObj(intChar -> String.valueOf((char) intChar))
  .forEach(charStringJoiner::add);
String joined = charStringJoiner.toString();
```

## 5.Apache Commons 语言

[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)库在StringUtils和ArrayUtils类中提供了一些方便的方法 ，我们可以使用它们来连接原始数组。

要使用这个库，我们需要将[commons-lang3](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")[依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")项添加 到我们的pom.xml中：

```plaintext
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

使用String分隔符时，我们将同时使用StringUtils和ArrayUtils。

让我们一起使用它们来连接我们的int数组：

```java
String joined = StringUtils.join(ArrayUtils.toObject(intArray), separator);
```

或者，如果我们使用原始char类型作为分隔符，我们可以简单地编写：

```java
String joined = StringUtils.join(intArray, separatorChar);
```

加入我们的 char数组的实现非常相似：

```java
String joined = StringUtils.join(ArrayUtils.toObject(charArray), separator);
```

当使用字符分隔符时：

```java
String joined = StringUtils.join(charArray, separatorChar);
```

## 6.番石榴

[Google 的 Guava](https://www.baeldung.com/guava-joiner-and-splitter-tutorial)库提供了我们可以用来连接数组的Joiner类。[要在我们的项目中 ](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")使用 Guava，我们需要添加[guava](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")[ Maven 依赖](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")项：

```plaintext
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

让我们使用Joiner类连接我们的 int数组：

```java
String joined = Joiner.on(separator).join(Ints.asList(intArray));
```

在这个例子中，我们还使用了 Guava 中的Ints.asList()方法，它很好地将基元数组转换为Integer列表。

Guava 提供了一种类似的方法来将 char数组转换为字符列表。因此，加入我们的char数组看起来非常像上面使用int数组的示例：

```java
String joined = Joiner.on(separator).join(Chars.asList(charArray));
```

## 7.字符串生成器

最后，如果我们不能使用Java8 或第三方库，我们可以使用StringBuilder手动连接元素数组。在这种情况下，两种类型的数组的实现是相同的：

```java
if (array.length == 0) {
    return "";
}
StringBuilder stringBuilder = new StringBuilder();
for (int i = 0; i < array.length - 1; i++) {
    stringBuilder.append(array[i]);
    stringBuilder.append(separator);
}
stringBuilder.append(array[array.length - 1]);
String joined = stringBuilder.toString();
```

## 八、总结

这篇快速文章说明了用给定的分隔符或字符串连接基元数组的多种方法。我们展示了使用本机 JDK 解决方案的示例，以及使用两个第三方库(Apache Commons Lang 和 Guava)的其他解决方案。