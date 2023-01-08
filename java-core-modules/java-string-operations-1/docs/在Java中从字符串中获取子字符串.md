## 1. 概述

在本快速教程中，我们将重点关注Java中字符串的子字符串功能。

我们将主要使用[String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)类中的方法，很少使用 Apache Commons 的 [StringUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html)类中的方法。

在以下所有示例中，我们将使用这个简单的字符串：

```java
String text = "Julia Evans was born on 25-09-1984. "
  + "She is currently living in the USA (United States of America).";
```

## 2.子串基础

让我们从一个非常简单的示例开始——提取具有起始索引的子字符串：

```java
assertEquals("USA (United States of America).", 
  text.substring(67));
```

请注意我们如何在此处的示例中提取 Julia 的居住国家/地区。

还有一个指定结束索引的选项，但如果没有它——子字符串将一直到字符串的末尾。 

在上面的例子中，让我们这样做并在最后去掉那个额外的点：

```java
assertEquals("USA (United States of America)", 
  text.substring(67, text.length() - 1));
```

在上面的示例中，我们使用了确切的位置来提取子字符串。

### 2.1. 获取从特定字符开始的子字符串

如果需要根据字符或字符串动态计算位置，我们可以使用indexOf方法：

```java
assertEquals("United States of America", 
  text.substring(text.indexOf('(') + 1, text.indexOf(')')));
```

可以帮助我们定位子字符串的类似方法是 lastIndexOf。让我们使用lastIndexOf来提取年份“1984”。它是最后一个破折号和第一个点之间的文本部分：

```java
assertEquals("1984",
  text.substring(text.lastIndexOf('-') + 1, text.indexOf('.')));
```

indexOf和lastIndexOf都可以将字符或字符串作为参数。让我们提取文本“USA”和括号中的其余文本：

```java
assertEquals("USA (United States of America)",
  text.substring(text.indexOf("USA"), text.indexOf(')') + 1));
```

## 3.使用子序列

String类提供了另一种称为subSequence的 方法，其作用类似于substring方法。

唯一的区别是它返回一个 [CharSequence](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/CharSequence.html)而不是一个 String并且它只能与特定的开始和结束索引一起使用：

```java
assertEquals("USA (United States of America)", 
  text.subSequence(67, text.length() - 1));
```

## 4.使用正则表达式

如果我们必须提取与特定模式匹配的子字符串，正则表达式会来拯救我们。

在示例字符串中， Julia 的出生日期格式为“dd-mm-yyyy”。我们可以使用Java正则表达式 API 来匹配这个模式。

首先，我们需要为“dd-mm-yyyy”创建一个模式：

```java
Pattern pattern = Pattern.compile("d{2}-d{2}-d{4}");
```

然后，我们将应用该模式从给定文本中查找匹配项：

```java
Matcher matcher = pattern.matcher(text);
```

匹配成功后，我们可以提取匹配的字符串：

```java
if (matcher.find()) {                                  
    Assert.assertEquals("25-09-1984", matcher.group());
}
```

有关Java正则表达式的更多详细信息，请查看[本](https://www.baeldung.com/regular-expressions-java)教程。

## 5.使用拆分

我们可以使用String类的split方法来提取子字符串。假设我们要从示例字符串中提取第一句话。使用split很容易做到这一点：

```java
String[] sentences = text.split(".");
```

由于 split 方法接受正则表达式，我们必须转义句点字符。现在结果是一个包含 2 个句子的数组。

我们可以使用第一句(或者遍历整个数组)：

```java
assertEquals("Julia Evans was born on 25-09-1984", sentences[0]);
```

请注意，有使用 Apache OpenNLP 进行句子检测和标记化的更好方法。查看[本](https://www.baeldung.com/apache-open-nlp)教程以了解有关 OpenNLP API 的更多信息。

## 6.使用扫描仪

我们一般使用[Scanner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Scanner.html)来使用正则表达式 来解析原始类型和字符串。扫描仪使用分隔符模式将其输入分解为标记，默认情况下匹配空格。

让我们看看如何使用它从示例文本中获取第一句话：

```java
try (Scanner scanner = new Scanner(text)) {
    scanner.useDelimiter(".");           
    assertEquals("Julia Evans was born on 25-09-1984", scanner.next());    
}
```

在上面的示例中，我们已将示例字符串设置为扫描器要使用的源。

然后我们将句点字符设置为分隔符(需要对其进行转义，否则在此上下文中它将被视为特殊的正则表达式字符)。

最后，我们断言此分隔输出中的第一个标记。

如果需要，我们可以使用while循环遍历整个标记集合。

```java
while (scanner.hasNext()) {
   // do something with the tokens returned by scanner.next()
}
```

## 7.Maven 依赖

我们可以更进一步，使用一个有用的实用程序——StringUtils类[——Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)库的一部分：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

[你可以在此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")找到该库的最新版本。

## 8. 使用StringUtils

Apache Commons 库添加了一些有用的方法来处理核心Java类型。Apache Commons Lang 为 java.lang API 提供了许多辅助实用程序，最著名的是字符串操作方法。

在此示例中，我们将了解如何提取嵌套在两个字符串之间的子字符串：

```java
assertEquals("United States of America", 
  StringUtils.substringBetween(text, "(", ")"));
```

如果子字符串嵌套在同一字符串的两个实例之间，则有此方法的简化版本：

```java
substringBetween(String str, String tag)
```

同一类中的substringAfter方法在第一次出现分隔符之后获取子字符串。

不返回分隔符：

```java
assertEquals("the USA (United States of America).", 
  StringUtils.substringAfter(text, "living in "));
```

同样，substringBefore方法获取第一次出现分隔符之前的子字符串。

不返回分隔符：

```java
assertEquals("Julia Evans", 
  StringUtils.substringBefore(text, " was born"));
```

你可以查看本教程以了解有关使用 Apache Commons Lang API 进行字符串处理的更多信息。

## 9.总结

在这篇快速文章中，我们发现了在Java中从字符串中提取子字符串的各种方法。你可以探索我们关于Java 中的字符串操作的[其他教程。](https://www.baeldung.com/java-string)