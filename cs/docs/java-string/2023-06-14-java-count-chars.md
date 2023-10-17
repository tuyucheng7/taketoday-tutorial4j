---
layout: post
title:  统计字符串中某个字符出现的次数
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

在Java中有很多方法可以计算字符串中字符的出现次数。

在这个简短的教程中，我们将重点介绍一些如何计算字符的示例-首先使用核心Java库，然后使用其他库和框架，例如Spring和Guava。

## 2. 使用核心Java库

### 2.1 命令式方法

一些开发人员可能更喜欢使用核心Java，有很多方法可以计算字符串中字符出现的次数。

让我们从一个简单的方法开始：

```java
String someString = "elephant";
char someChar = 'e';
int count = 0;
 
for (int i = 0; i < someString.length(); i++) {
    if (someString.charAt(i) == someChar) {
        count++;
    }
}
assertEquals(2, count);
```

毫不奇怪，这会起作用，但是有更好的方法可以做到这一点。

### 2.2 使用递归

一个不太明显但仍然有趣的解决方案是使用递归：

```java
private static int useRecursion(String someString, char searchedChar, int index) {
    if (index >= someString.length()) {
        return 0;
    }
    
    int count = someString.charAt(index) == searchedChar ? 1 : 0;
    return count + useRecursion(someString, searchedChar, index + 1);
}
```

我们可以通过以下方式调用此递归方法：useRecursionToCountChars("elephant", 'e', 0)。

### 2.3 使用正则表达式

另一种方法是使用正则表达式：

```java
Pattern pattern = Pattern.compile("[^e]*e");
Matcher matcher = pattern.matcher("elephant");
int count = 0;
while (matcher.find()) {
    count++;
}
 
assertEquals(2, count);
```

请注意，此解决方案在技术上是正确的，但不是最优的，因为使用强大的正则表达式来解决诸如查找字符串中字符出现次数这样简单的问题有点过分了。

### 2.4 使用Java 8特性

Java 8中可用的新特性在这里非常有用。

让我们使用Stream和lambda来实现计数：

```java
String someString = "elephant";
long count = someString.chars().filter(ch -> ch == 'e').count();
assertEquals(2, count);

long count2 = someString.codePoints().filter(ch -> ch == 'e').count();
assertEquals(2, count2);
```

因此，这显然是一个使用核心库的更清晰、更易读的解决方案。

## 3. 使用外部库

现在让我们看看一些使用外部库实用程序的解决方案。

### 3.1 使用StringUtils

通常，使用现有解决方案总是比编写我们自己的解决方案更好。commons.lang.StringUtils类为我们提供了countMatches()方法，该方法可用于计算给定String中的字符甚至子字符串。

首先，我们需要包含适当的依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons"ANDa%3A"commons-lang3")上找到最新版本。

现在让我们使用countMatches()来计算“elephant”字符串文本中e字符的数量：

```java
int count = StringUtils.countMatches("elephant", "e");
assertEquals(2, count);
```

### 3.2 使用Guava

Guava也有助于计算字符数，首先我们需要定义依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava"ANDa%3A"guava")上找到最新版本。

让我们看看番Guava如何快速帮助我们计算字符：

```java
int count = CharMatcher.is('e').countIn("elephant");
assertEquals(2, count);
```

### 3.3 使用Spring

当然，将Spring框架添加到我们的项目中只是为了计算字符数是没有意义的。

但是，如果我们的项目中已经有了它，我们只需要使用countOccurrencesOf()方法：

```java
int count = StringUtils.countOccurrencesOf("elephant", "e");
assertEquals(2, count);
```

## 4. 总结

在本文中，我们重点介绍了对String中的字符进行计数的各种方法。其中一些是使用纯Java实现的；有些需要额外的库。

我们的建议是使用StringUtils、Guava或Spring中已有的实用程序。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。