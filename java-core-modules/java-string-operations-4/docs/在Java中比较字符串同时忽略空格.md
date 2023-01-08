## 1. 概述

在这个简短的教程中，我们将了解如何在Java中忽略空格来比较字符串。

## 2.使用replaceAll()方法

假设我们有两个字符串——一个包含空格，另一个只包含非空格字符：

```java
String normalString = "ABCDEF";
String stringWithSpaces = " AB  CD EF ";
```

我们可以简单地比较它们，同时使用String类的内置replaceAll()方法忽略空格：

```java
assertEquals(normalString.replaceAll("s+",""), stringWithSpaces.replaceAll("s+",""));
```

使用上面的replaceAll()方法将删除我们字符串中的所有空格，包括不可见的字符，如制表符、n 等。

[除了 s+，我们还可以使用 s](https://www.baeldung.com/java-regex-s-splus)。

## 3. 使用 Apache Commons Lang

接下来，我们可以使用[Apache Commons Lang库中的](https://www.baeldung.com/java-commons-lang-3)StringUtils类来实现相同的目标。

此类有一个方法deleteWhitespace()，用于删除String中的所有空格：

```java
assertEquals(StringUtils.deleteWhitespace(normalString), StringUtils.deleteWhitespace(stringWithSpaces));
```

## 4.使用Spring框架的StringUtils类

最后，如果我们的项目已经在使用 Spring 框架，我们可以使用org.springframework.util包中的StringUtils类。

这次使用的方法是trimAllWhitespace()：

```java
assertEquals(StringUtils.trimAllWhitespace(normalString), StringUtils.trimAllWhitespace(stringWithSpaces));
```

我们应该记住，如果我们想要比较空格有意义的字符串，比如人名，我们不应该使用本文中的方法。例如，下面两个字符串将被认为是相等的：“JACKIE CHAN”和“JAC KIE CHAN”，这可能不是我们真正想要的。

## 5.总结

在本文中，我们看到了在Java中忽略空格的同时比较字符串的不同方法。