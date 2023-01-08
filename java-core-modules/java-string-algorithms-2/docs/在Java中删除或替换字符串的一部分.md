## 1. 概述

在本教程中，我们将研究可以在 Java中删除或替换部分字符串的各种方法。

我们将探索使用 String API 删除和/或替换子字符串，然后使用 StringBuilder API，最后使用 Apache Commons 库的 StringUtils类。

作为奖励，我们还将研究使用 String API 和 Apache Commons RegExUtils类替换确切的单词。

## 2.字符串接口

替换子字符串的最简单直接的方法之一是使用 String 类的 replace 、replaceAll 或 replaceFirst 。

replace()方法有 两个参数——目标和替换文本：

```java
String master = "Hello World Baeldung!";
String target = "Baeldung";
String replacement = "Java";
String processed = master.replace(target, replacement);
assertTrue(processed.contains(replacement));
assertFalse(processed.contains(target));
```

上面的代码片段将产生以下输出：

```plaintext
Hello World Java!
```

如果在选择目标时需要正则表达式，则 应该选择replaceAll()或 replaceFirst()方法。顾名思义， replaceAll() 将替换每个匹配的事件，而 replaceFirst()将替换第一个匹配的事件：

```java
String master2 = "Welcome to Baeldung, Hello World Baeldung";
String regexTarget = "(Baeldung)$";
String processed2 = master2.replaceAll(regexTarget, replacement);
assertTrue(processed2.endsWith("Java"));
```

processed2的值 将是：

```plaintext
Welcome to Baeldung, Hello World Java
```

这是因为作为 regexTarget提供的正则表达式将只匹配最后一次出现的 Baeldung。 在上面给出的所有示例中，我们可以使用一个空的替换，它会有效地从master中删除一个目标。

## 3.字符串生成器接口

我们还可以使用 StringBuilder类在Java中操作文本。 这里的两个方法是 delete()和 replace()。

我们可以从现有的String构造一个 StringBuilder 的实例，然后使用提到的方法根据需要执行String操作：

```java
String master = "Hello World Baeldung!";
String target = "Baeldung";
String replacement = "Java";

int startIndex = master.indexOf(target);
int stopIndex = startIndex + target.length();

StringBuilder builder = new StringBuilder(master);
```

现在我们可以 使用 delete() 删除目标：

```java
builder.delete(startIndex, stopIndex);
assertFalse(builder.toString().contains(target));
```

我们也可以使用 replace()来更新 master：

```java
builder.replace(startIndex, stopIndex, replacement);
assertTrue(builder.toString().contains(replacement));
```

使用StringBuilder 和 String API之间的一个明显区别 是我们必须自己获取 目标 String的开始和结束索引。

## 4.StringUtils类_

我们将考虑的另一种方法是 Apache Commons 库。

首先，让我们将所需的依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

可以在[此处找到该库的最新版本。](https://search.maven.org/classic/#search|ga|1|a%3Acommons-lang3 g%3Aorg.apache.commons)

StringUtils 类具有替换String的子字符串的 方法：

```java
String master = "Hello World Baeldung!";
String target = "Baeldung";
String replacement = "Java";

String processed = StringUtils.replace(master, target, replacement);
assertTrue(processed.contains(replacement));
```

replace()有一个重载变体，它采用整数 max参数，它确定要替换的出现次数。如果不考虑区分大小写，我们也可以使用replaceIgnoreCase()：

```java
String master2 = "Hello World Baeldung!";
String target2 = "baeldung";
String processed2 = StringUtils.replaceIgnoreCase(master2, target2, replacement);
assertFalse(processed2.contains(target));
```

## 5.替换确切的词

在最后一个示例中，我们将学习如何替换String中的确切单词。

执行此替换的直接方法是使用带单词边界[的正则表达式。](https://www.baeldung.com/regular-expressions-java)

单词边界正则表达式是b。将所需的词包含在这个正则表达式中将只匹配精确的出现。

首先，让我们看看如何将这个正则表达式与 String API 一起使用：

```java
String sentence = "A car is not the same as a carriage, and some planes can carry cars inside them!";
String regexTarget = "bcarb";
String exactWordReplaced = sentence.replaceAll(regexTarget, "truck");
```

exactWordReplaced字符串包含：

```plaintext
"A truck is not the same as a carriage, and some planes can carry cars inside them!"
```

只会替换确切的单词。请注意，在Java中使用正则表达式时，[始终需要转义反斜杠。](https://www.baeldung.com/java-regexp-escape-char)

执行此替换的另一种方法是使用 Apache Commons Library 中的RegExUtils类，它可以作为我们在上一节中看到的依赖项添加：

```java
String regexTarget = "bcarb";
String exactWordReplaced = RegExUtils.replaceAll(sentence, regexTarget, "truck");

```

虽然这两种方法都会产生相同的结果，但决定使用哪一种将取决于我们的具体情况。

## 六，总结

总之，我们探索了不止一种在Java中删除和替换子字符串的方法。应用的最佳方法在很大程度上仍然取决于当前的情况和背景。