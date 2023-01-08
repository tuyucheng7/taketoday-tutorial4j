## 1. 概述

在本教程中，我们将研究用换行符[拆分JavaString的不同方法。](https://www.baeldung.com/java-split-string)由于换行符在不同的操作系统中有所不同，我们将看看如何覆盖 Unix、Linux、Mac OS 9 及更早版本、macOS 和 Windows 操作系统。

## 2.换行分割字符串

### 2.1. 使用System#lineSeparator方法按换行符拆分字符串

鉴于换行符在各种操作系统中是不同的，当我们希望我们的代码与平台无关时，我们可以使用系统定义的常量或方法。

[System#lineSeparator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#lineSeparator())方法返回底层操作系统的行分隔符字符串。它返回系统属性line.separator的值。

因此，我们可以使用System#lineSeparator方法返回的行分隔符字符串与String#split方法一起使用换行符拆分JavaString：

```java
String[] lines = "Line1rnLine2rnLine3".split(System.lineSeparator());
```

结果行将是：

```java
["Line1", "Line2", "Line3"]
```

### 2.2. 使用正则表达式换行拆分字符串

接下来，让我们先看看不同操作系统中用于分隔行的不同字符。

“ n ”字符在 Unix、Linux 和 macOS 中分隔行。另一方面，“ rn ”字符在 Windows 环境中分隔行。最后，“ r ”字符在 Mac OS 9 及更早版本中分隔行。

因此，我们需要在使用正则表达式按换行符拆分字符串时处理所有可能的换行符。

最后，让我们看看涵盖所有不同操作系统换行符的正则表达式模式。也就是说，我们需要寻找“n”、“rn”和“r”模式。这可以通过[在Java中使用正则表达式](https://www.baeldung.com/regular-expressions-java)轻松完成。

涵盖所有不同换行符的正则表达式模式将是：

```java
"r?n|r"
```

分解它，我们看到：

-   n = Unix、Linux 和 macOS 模式
-   rn = Windows 环境模式
-   r = MacOS 9 及更早版本模式

接下来，让我们使用String #split方法来拆分Java String。让我们看几个例子：

```java
String[] lines = "Line1nLine2nLine3".split("r?n|r");
String[] lines = "Line1rLine2rLine3".split("r?n|r");
String[] lines = "Line1rnLine2rnLine3".split("r?n|r");
```

所有示例的结果行将是：

```java
["Line1", "Line2", "Line3"]
```

### 2.3. 在Java8 中按换行符拆分字符串

Java 8 提供了一个“R”模式，可以匹配任何 Unicode 换行符序列，并涵盖不同操作系统的所有换行符。因此，我们可以在Java8 或更高版本中使用“R”模式代替“r?n|r” 。

让我们看几个例子：

```java
String[] lines = "Line1nLine2nLine3".split("R");
String[] lines = "Line1rLine2rLine3".split("R");
String[] lines = "Line1rnLine2rnLine3".split("R");
```

同样，所有示例的结果输出行将是：

```xml
["Line1", "Line2", "Line3"]
```

### 2.4. 使用模式类按换行符拆分字符串

在Java8 中，Pattern类带有一个方便的[splitAsStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#splitAsStream(java.lang.CharSequence))方法。

在我们的例子中，我们可以使用“R”模式，但当然，这种方法也可以用于通过任何更复杂的[正则表达式来拆分](https://www.baeldung.com/regular-expressions-java)String。

让我们看看它的实际效果：

```java
Pattern pattern = Pattern.compile("R");
Stream<String> lines = pattern.splitAsStream("Line1nLine2nLine3");
Stream<String> lines = pattern.splitAsStream("Line1rLine2rLine3");
Stream<String> lines = pattern.splitAsStream("Line1rnLine2rnLine3");
```

正如我们所见，这一次，我们得到的不是数组，而是String的Stream，我们可以轻松地对其进行进一步处理。

### 2.5. 在Java11 中按换行符拆分字符串

Java 11 使按换行符拆分变得非常容易：

```java
Stream<String> lines = "Line1nLine2rLine3rnLine4".lines();
```

因为lines()在底层使用“R”模式，所以它适用于所有类型的行分隔符。

正如我们所看到的，很难找到一种更简单的方法来按换行符拆分字符串！

## 3.总结

在这篇简短的文章中，我们了解了在不同操作系统中可能遇到的不同换行符。此外，我们了解了如何使用我们自己的正则表达式模式以及使用从Java8 开始可用的“R”模式按换行符拆分Java字符串。