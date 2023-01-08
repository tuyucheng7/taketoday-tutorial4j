## 1. 概述

在本教程中，我们将讨论在Java中检查空字符串或空白字符串的一些方法。有一些本地语言方法，以及几个库。

## 2.空与空白

当然，知道字符串何时为空或空白是很常见的，但让我们确保我们与我们的定义在同一页上。

如果字符串为null或没有任何长度的字符串，我们认为该字符串为空。如果一个字符串只包含空格，那么我们称它为blank。

对于 Java，空格是字符，如空格、制表符等。我们可以查看[Character.isWhitespace](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#isWhitespace(char))以获取示例。

## 3.空字符串

### 3.1. 使用Java6 及更高版本

如果我们至少使用Java6，那么检查空字符串的最简单方法是[String#isEmpty](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#isEmpty())：

```java
boolean isEmptyString(String string) {
    return string.isEmpty();
}
```

为了使其也空安全，我们需要添加一个额外的检查：

```java
boolean isEmptyString(String string) {
    return string == null || string.isEmpty();
}
```

### 3.2. 使用Java5 及以下版本

String#isEmpty是在Java6 中引入的。对于Java5 及以下版本，我们可以使用String#length代替：

```java
boolean isEmptyString(String string) {
    return string == null || string.length() == 0;
}
```

事实上，String#isEmpty只是String#length的快捷方式。

## 4.空白字符串

String#isEmpty和String#length都可以用来检查空字符串。

如果我们还想检测空白字符串，我们可以借助String#trim来实现。它会在执行检查之前删除所有前导和尾随空格：

```java
boolean isBlankString(String string) {
    return string == null || string.trim().isEmpty();
}
```

准确地说，String#trim将删除所有[Unicode 代码小于或等于 U+0020](https://en.wikipedia.org/wiki/List_of_Unicode_characters#Control_codes)的前导和尾随字符。

而且，请记住String是不可变的，因此调用 trim 实际上不会更改底层字符串。

除了上述方法，从Java11 开始，我们还可以使用 [isBlank()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#isBlank())方法代替 trimming：

```java
boolean isBlankString(String string) {
    return string == null || string.isBlank();
}
```

isBlank ()方法也更有效一些，因为它不会在堆上创建新的 String。因此，如果我们使用的是Java11 或更高版本，这是首选方法。

## 5.Bean 验证

检查空白字符串的另一种方法是正则表达式。例如，这与[Java Bean Validation](https://www.baeldung.com/javax-validation)一起派上用场：

```java
@Pattern(regexp = "A(?!sZ).+")
String someString;
```

给定的正则表达式确保不会验证空字符串或空白字符串。

## 6. 使用 Apache Commons

如果可以添加依赖项，我们可以使用[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)。这有许多Java助手。

如果我们使用 Maven，我们需要将[commons -lang3依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)项添加 到我们的 pom 中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

除其他外，这给了我们[StringUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html)。

此类带有[isEmpty](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#isEmpty-java.lang.CharSequence-)、[isBlank](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#isBlank-java.lang.CharSequence-)等方法：

```java
StringUtils.isBlank(string)
```

此调用与我们自己的isBlankString方法的作用相同。它是 null 安全的，还会检查空格。

## 7.番石榴

另一个带有某些字符串相关实用程序的著名库是 Google 的[Guava](https://github.com/google/guava)。从 23.1 版开始，Guava 有两种风格：android 和jre。Android 风格针对 Android 和Java7，而 JRE 风格针对Java8。

如果我们不以 Android 为目标，我们可以将[JRE 风格](https://search.maven.org/artifact/com.google.guava/guava/31.0.1-jre/jar)添加到我们的 pom 中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

Guavas Strings 类带有方法[Strings.isNullOrEmpty](https://google.github.io/guava/releases/27.1-jre/api/docs/com/google/common/base/Strings.html#isNullOrEmpty-java.lang.String-)：

```java
Strings.isNullOrEmpty(string)
```

它检查给定的字符串是否为 null 或空，但不会检查纯空格字符串。

## 八、总结

有几种方法可以检查字符串是否为空。通常，我们还想检查一个字符串是否为空白，这意味着它只包含空白字符。

最方便的方法是使用 Apache Commons Lang，它提供了诸如StringUtils.isBlank之类的帮助程序。如果我们想坚持使用纯 Java，我们可以将String#trim与 String#isEmpty或String#length结合使用。对于 Bean Validation，可以改用正则表达式。