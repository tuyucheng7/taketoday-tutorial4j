## 1. 概述

在某些情况下，我们需要断言给定的字符串是否为空。在Java中有很多方法可以做这样的断言。

让我们在本快速教程中探索其中的一些测试断言技术。

## 2.Maven依赖

我们需要先获取一些依赖项。在 Maven 项目中，我们可以将以下依赖项添加到pom.xml中：

[单元](https://search.maven.org/classic/#search|gav|1|g%3A"junit" AND a%3A"junit")：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

[Hamcrest 核心](https://search.maven.org/classic/#search|gav|1|g%3A"org.hamcrest" AND a%3A"hamcrest-core")：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-core</artifactId>
    <version>1.3</version>
</dependency>
```

[Apache Commons 语言](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

[断言](https://search.maven.org/classic/#search|gav|1|g%3A"org.assertj" AND a%3A"assertj-core")：

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.11.1</version>
</dependency>
```

[谷歌番石榴](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

## 3. 使用 JUnit

我们将使用[String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)类的[isEmpty](https://www.baeldung.com/string/is-empty)方法和JUnit 的[Assert](http://junit.sourceforge.net/javadoc/org/junit/Assert.html)类来验证给定的String是否不为空。由于 如果输入字符串为空， isEmpty方法会返回 true，因此我们可以将它与 assertFalse方法一起使用：

```java
assertFalse(text.isEmpty());
```

或者，我们也可以使用：

```java
assertTrue(!text.isEmpty());
```

考虑到文本可能为空，另一种方法是使用 assertNotEquals 方法进行相等性检查：

```java
assertNotEquals("", text);
```

要么：

```java
assertNotSame("", text);
```

[在此处](https://www.baeldung.com/junit-assertions)查看我们关于 JUnit 断言的深入指南。

所有这些断言在失败时都将返回AssertionError。

## 4. 使用 Hamcrest 核心

[Hamcrest](http://hamcrest.org/) 是一个著名的框架，提供通常用于Java生态系统中的单元测试的匹配器。

我们可以使用 Hamcrest CoreMatchers 类进行空字符串检查：

```java
assertThat(text, CoreMatchers.not(isEmptyString()));
```

IsEmptyString类中提供了isEmptyString[方法](http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/text/IsEmptyString.html)。

这也会在失败时返回 AssertionError，但输出更有用：

```plaintext
java.lang.AssertionError: 
Expected: not an empty string
     but: was ""
```

如果需要，要验证 String 既不为空也不为空，我们可以使用isEmptyOrNullString：

```java
assertThat(text, CoreMatchers.not(isEmptyOrNullString()));
```

要了解CoreMatchers类的其他方法，请阅读[这篇](https://www.baeldung.com/hamcrest-core-matchers)先前发表的文章。

## 5. 使用 Apache Commons Lang

[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)库为java.lang API提供了许多辅助实用程序。

StringUtils类提供了一种我们可以用来检查空字符串的方法：

```java
assertTrue(StringUtils.isNotBlank(text));
```

失败时，这将返回一个简单的AssertionError。

要了解有关使用 Apache Commons Lang 进行字符串处理的更多信息，请阅读[本文](https://www.baeldung.com/string-processing-commons-lang) 。

## 6. 使用 AssertJ

[AssertJ](https://joel-costigliola.github.io/assertj/) 是一个开源的、社区驱动的库，用于在Java测试中编写流畅且丰富的断言。

AbstractCharSequenceAssert.isNotEmpty()方法 验证实际的CharSequence不为空，或者换句话说，它不为 null 且长度为 1 或更多：

```java
Assertions.assertThat(text).isNotEmpty()
```

失败时，打印输出：

```plaintext
java.lang.AssertionError: 
Expecting actual not to be empty
```

[我们在这里](https://www.baeldung.com/introduction-to-assertj)有一篇关于 AssertJ 的不错的介绍性文章。

## 7. 使用谷歌番石榴

[Guava](https://github.com/google/guava)是 Google 提供的一组核心库。

Guava Strings类中的方法 isNullOrEmpty可用于验证字符串是否为空(或 null)：

```java
assertFalse(Strings.isNullOrEmpty(text));
```

当失败且没有其他输出消息时，这也会返回AssertionError 。

要探索我们关于 Guava API 的其他文章，请点击 [此处](https://www.baeldung.com/category/guava/)的链接。

## 八、总结

在本快速教程中，我们了解了如何断言给定字符串是否为空。