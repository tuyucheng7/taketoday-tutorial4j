---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

StringJoiner是Java 8在java.util包下新增的类。

简而言之，它可用于使用定界符、前缀和后缀拼接字符串。

## 2. 添加元素

我们可以使用add()方法添加字符串：

```java
@Test
public void whenAddingElements_thenJoinedElements() {
    StringJoiner joiner = new StringJoiner(",", PREFIX, SUFFIX);
    joiner.add("Red")
        .add("Green")
        .add("Blue");

    assertEquals(joiner.toString(), "[Red,Green,Blue]");
}
```

如果我们想加入列表的所有元素，我们将不得不遍历列表。不幸的是，没有简单的方法可以使用StringJoiner来做到这一点：

```java
@Test
public void whenAddingListElements_thenJoinedListElements() {
    List<String> rgbList = new ArrayList<>();
    rgbList.add("Red");
    rgbList.add("Green");
    rgbList.add("Blue");

    StringJoiner rgbJoiner = new StringJoiner(",", PREFIX, SUFFIX);

    for (String color : rgbList) {
        rgbJoiner.add(color);
    }

    assertEquals(rgbJoiner.toString(), "[Red,Green,Blue]");
}
```

## 3. 施工

要构造StringJoiner的实例，我们需要提及分隔符。可选地，我们还可以指定结果中应该出现的前缀和后缀：

```java
private String PREFIX = "[";
private String SUFFIX = "]";

@Test
public void whenEmptyJoinerWithoutPrefixSuffix_thenEmptyString() {
    StringJoiner joiner = new StringJoiner(",");
 
    assertEquals(0, joiner.toString().length());
}

@Test
public void whenEmptyJoinerJoinerWithPrefixSuffix_thenPrefixSuffix() {
    StringJoiner joiner = new StringJoiner(",", PREFIX, SUFFIX);
 
    assertEquals(joiner.toString(), PREFIX + SUFFIX);
}
```

我们使用toString()从拼接器中获取当前值。

注意拼接器返回的默认值。没有前缀和后缀的拼接器返回空字符串，而带有前缀和后缀的拼接器返回包含前缀和后缀的字符串。

我们可以使用setEmptyValue()更改返回的默认字符串：

```java
@Test
public void whenEmptyJoinerWithEmptyValue_thenDefaultValue() {
    StringJoiner joiner = new StringJoiner(",");
    joiner.setEmptyValue("default");

    assertEquals(joiner.toString(), "default");
}

@Test
public void whenEmptyJoinerWithPrefixSuffixAndEmptyValue_thenDefaultValue() {
    StringJoiner joiner = new StringJoiner(",", PREFIX, SUFFIX);
    joiner.setEmptyValue("default");

    assertEquals(joiner.toString(), "default");
}
```

在这里，两个拼接器都返回EMPTY_JOINER常量。

仅当StringJoiner为空时才返回默认值。

## 4. 合并加入者

我们可以使用merge()合并两个拼接器。它将不带前缀和后缀的给定StringJoiner的内容添加为下一个元素：

```java
@Test
public void whenMergingJoiners_thenReturnMerged() {
    StringJoiner rgbJoiner = new StringJoiner(",", PREFIX, SUFFIX);
    StringJoiner cmybJoiner = new StringJoiner("-", PREFIX, SUFFIX);

    rgbJoiner.add("Red")
        .add("Green")
        .add("Blue");
    cmybJoiner.add("Cyan")
        .add("Magenta")
        .add("Yellow")
        .add("Black");

    rgbJoiner.merge(cmybJoiner);

    assertEquals(
        rgbJoiner.toString(), 
        "[Red,Green,Blue,Cyan-Magenta-Yellow-Black]");
}
```

请注意“-”如何用于拼接cmybJoiner的内容，而rgbJoiner仍然使用“,”。

## 5. 流接口

这几乎就是我们可以用StringJoiner做的所有事情。

在Stream API中可以找到另一种间接用法：

```java
@Test
public void whenUsedWithinCollectors_thenJoined() {
    List<String> rgbList = Arrays.asList("Red", "Green", "Blue");
    String commaSeparatedRGB = rgbList.stream()
        .map(color -> color.toString())
        .collect(Collectors.joining(","));

    assertEquals(commaSeparatedRGB, "Red,Green,Blue");
}
```

Collectors.joining()在内部使用StringJoiner来执行拼接操作。

## 6. 总结

在这个简短的教程中，我们说明了如何使用StringJoiner类。总的来说，StringJoiner看起来非常原始，无法解决一些基本用例，例如拼接列表的元素。它似乎主要是为收藏家设计的。

如果StringJoiner不能满足我们的要求，还有其他流行且功能强大的库，例如Guava。
与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。
