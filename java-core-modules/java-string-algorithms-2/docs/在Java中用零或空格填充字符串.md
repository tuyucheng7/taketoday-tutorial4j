## 1. 概述

在这个简短的教程中，我们将了解如何在 Java中填充字符串。我们将主要关注左垫，这意味着我们将向其添加前导空格或零，直到它达到所需的长度。

右填充字符串的方法非常相似，所以我们只指出不同之处。

## 2.使用自定义方法填充字符串

Java 中的String类没有提供方便的填充方法，所以让我们自己创建几个方法。

首先，让我们设定一些期望：

```java
assertEquals("    123456", padLeftZeros("123456", 10));
assertEquals("0000123456", padLeftZeros("123456", 10));
```

### 2.1. 使用StringBuilder

我们可以使用StringBuilder和一些过程逻辑来实现这一点 ：

```java
public String padLeftZeros(String inputString, int length) {
    if (inputString.length() >= length) {
        return inputString;
    }
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length - inputString.length()) {
        sb.append('0');
    }
    sb.append(inputString);

    return sb.toString();
}
```

我们可以在这里看到，如果原始文本的长度等于或大于所需长度，我们将返回其未更改的版本。否则，我们创建一个新的String，以空格开头，并添加原始的。

当然，如果我们想用不同的字符填充，我们可以只使用它而不是0。

同样，如果我们想要右填充，我们只需要改为执行 new StringBuilder(inputString)，然后在末尾添加空格。

### 2.2. 使用子字符串

另一种进行左填充的方法是创建一个只包含填充字符的所需长度的字符串，然后使用substring()方法：

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < length; i++) {
    sb.append(' ');
}

return sb.substring(inputString.length()) + inputString;
```

### 2.3. 使用String.format

最后，从Java5 开始，我们可以使用String .format()：

```java
return String.format("%1$" + length + "s", inputString).replace(' ', '0');
```

我们应该注意，默认情况下，填充操作将使用空格执行。这就是为什么我们需要使用replace()方法来填充零或任何其他字符。

对于正确的 pad，我们只需要使用不同的标志： %1$-。

## 3.使用库填充字符串

此外，还有一些外部库已经提供了填充功能。

### 3.1. Apache Commons 语言

Apache Commons Lang 提供了一组Java实用程序类。最受欢迎的之一是[StringUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html)。

要使用它，我们需要通过将[它的依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")项添加到我们的 pom.xml文件来将它包含到我们的项目中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

然后我们传递inputString和 length，就像我们创建的方法一样。

我们还可以传递填充字符：

```java
assertEquals("    123456", StringUtils.leftPad("123456", 10));
assertEquals("0000123456", StringUtils.leftPad("123456", 10, "0"));
```

同样，默认情况下String将用空格填充，或者我们需要显式设置另一个填充字符。

还有对应的rightPad()方法。

要探索 Apache Commons Lang 3 的更多功能，请查看[我们的介绍性教程](https://www.baeldung.com/java-commons-lang-3)。要查看使用StringUtils类进行字符串操作的其他方式，请参阅[本文](https://www.baeldung.com/string-processing-commons-lang)。

### 3.2. 谷歌番石榴

我们可以使用的另一个库是 Google 的[Guava](https://github.com/google/guava)。

当然，我们首先需要通过添加[它的依赖](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")来将它添加到项目中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

然后我们使用Strings类：

```java
assertEquals("    123456", Strings.padStart("123456", 10, ' '));
assertEquals("0000123456", Strings.padStart("123456", 10, '0'));
```

这个方法没有默认的pad字符，所以每次都需要传入。

对于右垫，我们可以使用padEnd()方法。

Guava 库提供了更多的特性，我们已经介绍了很多。在此处查找[与 Guava 相关的文章](https://www.baeldung.com/category/guava/)。

## 4。总结

在这篇简短的文章中，我们说明了如何在 Java中填充字符串。我们展示了使用我们自己的实现或现有库的示例。