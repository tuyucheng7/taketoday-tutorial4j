---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

在本教程中，我们将介绍Java[String](https://www.tuyucheng.com/java-string)类中包含的toUpperCase和toLowerCase方法。

我们将从创建一个名为name的字符串开始：

```java
String name = "John Doe";
```

## 2. 转换为大写

要基于name创建一个新的大写字符串，我们调用toUpperCase方法：

```java
String uppercaseName = name.toUpperCase();
```

这导致uppercaseName的值为“JOHNDOE”：

```java
assertEquals("JOHN DOE", uppercaseName);
```

请注意，字符串在Java中是不可变的，调用toUpperCase会创建一个新的字符串。换句话说，调用toUpperCase时name没有改变。

## 3. 转换为小写

类似地，我们通过调用toLowerCase基于名称创建一个新的小写字符串：

```java
String lowercaseName = name.toLowerCase();
```

这导致lowercaseName的值为“johndoe”：

```java
assertEquals("john doe", lowercaseName);
```

与toUpperCase一样，toLowerCase不会更改name的值。

## 4. 使用语言环境更改大小写

此外，通过为toUpperCase和toLowerCase方法提供[Locale](https://www.tuyucheng.com/java-8-localization#localization)，我们可以使用特定于语言环境的规则更改String的大小写。

例如，我们可以提供Locale以大写土耳其语i(Unicode0069)：

```java
Locale TURKISH = new Locale("tr");
System.out.println("u0069".toUpperCase());
System.out.println("u0069".toUpperCase(TURKISH));
```

因此，这会导致大写I和带点的大写I：

```plaintext
I
İ
```

我们可以使用以下断言来验证这一点：

```java
assertEquals("u0049", "u0069".toUpperCase());
assertEquals("u0130", "u0069".toUpperCase(TURKISH));
```

同样，我们可以使用土耳其语I(Unicode0049)对toLowerCase做同样的事情：

```java
System.out.println("u0049".toLowerCase());
System.out.println("u0049".toLowerCase(TURKISH));
```

因此，这会导致小写i和小写无点i：

```plaintext
i
ı
```

我们可以使用以下断言来验证这一点：

```java
assertEquals("u0069", "u0049".toLowerCase());
assertEquals("u0131", "u0049".toLowerCase(TURKISH));
```

## 5. 总结

总之，Java String类包括用于更改String大小写的toUpperCase和toLowerCase方法。如果需要，可以提供Locale以在更改String的大小写时提供特定于区域设置的规则。
与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。
