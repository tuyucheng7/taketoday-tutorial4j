## 1. 概述

将 [Java String转换为double](https://www.baeldung.com/java-string-to-double)时， 我们通常会使用 Double.parseDouble(String value) 方法。此方法允许我们 将给定 双精度值(例如“2.0”)的字符串表示形式转换为原始双精度 值。

与大多数方法调用一样，最好避免传递空引用，这可能会 在运行时导致NullPointerException 。

在本教程中，我们将探索 在调用Double.parseDouble之前检查null 的几种方法。在查看一些外部库之前，我们将首先考虑使用核心Java的解决方案。

## 2. 为什么要检查

首先，让我们了解如果我们在解析 S tring时不检查空值会发生什么。让我们从传递一个空字符串开始：

```java
double emptyString = Double.parseDouble("");
```

当我们运行这段代码时，它会抛出一个java.lang.NumberFormatException：

```plaintext
Exception in thread "main" java.lang.NumberFormatException: empty String
	at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:1842)
	at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
	at java.lang.Double.parseDouble(Double.java:538)
	...
```

现在让我们考虑传递一个空引用：

```java
double nullString = Double.parseDouble(null);
```

不出所料，这次会抛出java.lang.NullPointerException ：

```plaintext
Exception in thread "main" java.lang.NullPointerException
	at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:1838)
	at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
	at java.lang.Double.parseDouble(Double.java:538)
	...
```

正如我们所知，在我们的应用程序代码中使用异常是一种很好的做法。但总的来说，我们应该避免这些类型的[未经检查](https://www.baeldung.com/java-checked-unchecked-exceptions)的异常，它们很可能是编程错误的结果。

## 3. 如何检查 Core Java

在本节中，我们将了解使用核心 Java检查null或空值的几个选项。

### 3.1. 使用香草 Java

让我们从定义一个简单的方法开始，该方法将检查我们传递的值是null还是空String：

```java
private static double parseStringToDouble(String value) {
    return value == null || value.isEmpty() ? Double.NaN : Double.parseDouble(value);
}
```

如我们所见，如果我们尝试解析的值是null或空，则此方法返回[的不是数字](https://www.baeldung.com/java-not-a-number)。否则，我们调用Double.parseDouble方法。

我们可以将此示例更进一步，并提供提供预定义默认值的可能性：

```java
private static double parseStringToDouble(String value, double defaultValue) {
    return value == null || value.isEmpty() ? defaultValue : Double.parseDouble(value);
}
```

当我们调用此方法时，我们会提供一个适当的默认值以在提供的值为null或空时返回：

```java
assertThat(parseStringToDouble("1", 2.0d)).isEqualTo(1.0d);
assertThat(parseStringToDouble(null, 1.0d)).isEqualTo(1.0d);
assertThat(parseStringToDouble("", 1.0d)).isEqualTo(1.0d);
```

### 3.2. 使用可选

现在让我们看一下使用[Optional](https://www.baeldung.com/java-optional)的不同解决方案：

```java
private static Optional parseStringToOptionalDouble(String value) {
    return value == null || value.isEmpty() ? Optional.empty() : Optional.of(Double.valueOf(value));
}
```

这一次，我们使用Optional作为[返回类型](https://www.baeldung.com/java-optional-return)。因此，当我们调用这个方法时，我们就有可能调用标准方法，例如isPresent()和isEmpty()来确定一个值是否存在：

```java
parseStringToOptionalDouble("2").isPresent()
```

我们还可以使用Optional的orElse方法返回默认值：

```java
parseStringToOptionalDouble("1.0").orElse(2.0d) 
parseStringToOptionalDouble(null).orElse(2.0d) 
parseStringToOptionalDouble("").orElse(2.0d)
```

## 4. 外部图书馆

现在我们已经很好地理解了如何使用核心Java检查null和空值，让我们来看看一些外部库。

### 4.1. 谷歌番石榴

我们要看的第一个外部解决方案是[Google Guava](https://www.baeldung.com/whats-new-in-guava-19)，它在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")上可用：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

我们可以简单地使用Doubles.tryParse方法：

```java
Doubles.tryParse(MoreObjects.firstNonNull("1.0", "2.0"))
Doubles.tryParse(MoreObjects.firstNonNull(null, "2.0"))
```

在此示例中，我们还使用了MoreObjects.firstNonNull方法，该方法将返回两个给定参数中的第一个不为null的参数。

这段代码在大多数情况下都可以正常工作，但让我们想象一个不同的例子：

```java
Doubles.tryParse(MoreObjects.firstNonNull("", "2.0"))
```

在这种情况下，由于空字符串不是null，该方法将返回null而不是抛出NumberFormatException。我们避免了异常，但我们仍然必须在应用程序代码中的某个时刻处理空值。

### 4.2. Apache Commons Lang NumberUtils

NumberUtils 类提供了许多有用的实用程序，可以更轻松地处理数字。

[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/) 工件可从 Maven [Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")获得：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

然后我们可以简单地使用 NumberUtils 中的 toDouble方法：

```java
NumberUtils.toDouble("1.0")
NumberUtils.toDouble("1.0", 1.0d) 

```

在这里，我们有两个选择：

-   将String转换为double ，如果转换失败则返回0.0d
-   将String转换为double，如果转换失败则提供定义的默认值

如果我们传递空值或null值，则默认返回0.0d：

```java
assertThat(NumberUtils.toDouble("")).isEqualTo(0.0d);
assertThat(NumberUtils.toDouble(null)).isEqualTo(0.0d);
```

这比前面的例子要好，因为无论转换过程中发生什么，我们总是得到一个双倍的返回值。

### 4.3. Vavr

最后但同样重要的是，让我们看一下[vavr.io](https://www.baeldung.com/vavr)，它提供了一种函数式方法。

与往常一样，可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"io.vavr" AND a%3A"vavr")上找到该工件：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.10.2</version>
</dependency>
```

同样，我们将定义一个使用 vavr [Try](https://www.baeldung.com/vavr-try)类的简单方法：

```java
public static double tryStringToDouble(String value, double defaultValue) {
    return Try.of(() -> Double.parseDouble(value)).getOrElse(defaultValue);
}

```

我们将以与其他示例完全相同的方式调用此方法：

```java
assertThat(tryStringToDouble("1", 2.0d)).isEqualTo(1.0d);
assertThat(tryStringToDouble(null, 2.0d)).isEqualTo(2.0d);
assertThat(tryStringToDouble("", 2.0d)).isEqualTo(2.0d);
```

## 5.总结

在本快速教程中，我们探讨了在调用Double.parseDouble方法之前检查null和空字符串的几种方法。