## 1. 概述

在这篇简短的文章中，我们将展示如何在Java中反转数组。

我们将看到几种不同的方法来使用基于纯Java8 的解决方案来实现这一点——其中一些改变现有数组，一些创建一个新数组。

接下来，我们将研究两种使用外部库的解决方案——一种使用Apache Commons Lang，另一种使用Google Guava。

## 2. 定义问题

基本思想是颠倒数组中元素的顺序。所以，如果给定数组：

```java
fruits = {"apples", "tomatoes", "bananas", "guavas", "pineapples"}
```

我们想得到：

```java
invertedFruits = {"pineapples", "guavas", "bananas", "tomatoes",  "apples"}
```

让我们看看我们可以做到的一些方法。

## 3. 使用传统的for循环

我们可能会考虑反转数组的第一种方法是使用for循环：

```java
void invertUsingFor(Object[] array) {
    for (int i = 0; i < array.length / 2; i++) {
        Object temp = array[i];
        array[i] = array[array.length - 1 - i];
        array[array.length - 1 - i] = temp;
    }
}
```

正如我们所见，代码遍历了数组的一半，改变了对称位置的元素。

我们使用了一个临时变量，这样我们就不会在迭代过程中丢失数组当前位置的值。

## 4. 使用Java8 Stream API

我们还可以使用 Stream API 反转数组：

```java
Object[] invertUsingStreams(Object[] array) {
    return IntStream.rangeClosed(1, array.length)
      .mapToObj(i -> array[array.length - i])
      .toArray();
}
```

这里我们使用IntStream.range方法来生成一个连续的数字流。然后我们将这个序列按降序映射到数组索引中。

## 5. 使用Collections.reverse()

让我们看看如何使用Collections.reverse()方法反转数组：

```java
public void invertUsingCollectionsReverse(Object[] array) {
    List<Object> list = Arrays.asList(array);
    Collections.reverse(list);
}
```

与前面的例子相比，这是一种更具可读性的完成任务的方式。

## 6. 使用 Apache Commons Lang

反转数组的另一种选择是使用Apache Commons Lang库。要使用它，我们必须首先将库作为依赖项包含在内：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

最新版本的Commons Lang可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")找到。

让我们使用ArrayUtils类来反转数组：

```java
public void invertUsingCommonsLang(Object[] array) {
    ArrayUtils.reverse(array);
}
```

正如我们所见，这个解决方案非常简单。

## 7. 使用谷歌番石榴

另一种选择是使用Google Guava库。就像我们对Commons Lang所做的一样，我们将把库作为依赖项包含在内：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

[最新版本可以在Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")找到。

然后，我们可以使用Guava 的Lists类中的reverse方法来反转数组：

```java
public Object[] invertUsingGuava(Object[] array) {
    List<Object> list = Arrays.asList(array);
    List<Object> reversed = Lists.reverse(list);
    return reversed.toArray();
}
```

## 八、总结

在本文中，我们研究了几种在Java中反转数组的不同方法。我们展示了一些仅使用核心Java的解决方案和另外两个使用第三方库的解决方案——Commons Lang和Guava。