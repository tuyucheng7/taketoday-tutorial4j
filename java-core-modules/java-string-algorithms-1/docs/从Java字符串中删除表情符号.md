## 1. 概述

如今，表情符号在短信中越来越流行——有时我们需要清除文本中的表情符号和其他符号。

在本教程中，我们将讨论在Java中从字符串中删除表情符号的不同方法。

## 2.使用表情符号库

首先，我们将使用表情符号库从我们的String中删除表情符号。

我们将在以下示例中使用emoji-java，因此我们需要将此依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.vdurmont</groupId>
    <artifactId>emoji-java</artifactId>
    <version>4.0.0</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/search?q=emoji-java)找到。

现在让我们看看如何使用 emoji-java 从我们的String中删除表情符号：

```java
@Test
public void whenRemoveEmojiUsingLibrary_thenSuccess() {
    String text = "la conférence, commencera à 10 heures ?";
    String result = EmojiParser.removeAllEmojis(text);

    assertEquals(result, "la conférence, commencera à 10 heures ");
}
```

在这里，我们调用EmojiParser的removeAllEmojis()方法。

我们还可以使用 EmojiParser使用parseToAliases()方法用别名替换表情符号 ：

```java
@Test
public void whenReplaceEmojiUsingLibrary_thenSuccess() {
    String text = "la conférence, commencera à 10 heures ?";
    String result = EmojiParser.parseToAliases(text);

    assertEquals(
      result, 
      "la conférence, commencera à 10 heures :sweat_smile:");
}
```

请注意，如果我们需要用别名替换表情符号，使用此库非常有用。

但是，emoji-java 库只能检测表情符号，无法检测符号或其他特殊字符。

## 3.使用正则表达式

接下来，我们可以使用正则表达式来删除表情符号和其他符号。
我们将只允许特定类型的字符：

```java
@Test
public void whenRemoveEmojiUsingMatcher_thenSuccess() {
    String text = "la conférence, commencera à 10 heures ?";
    String regex = "[^p{L}p{N}p{P}p{Z}]";
    Pattern pattern = Pattern.compile(
      regex, 
      Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(text);
    String result = matcher.replaceAll("");

    assertEquals(result, "la conférence, commencera à 10 heures ");
}
```

让我们分解一下我们的正则表达式：

-   p{L} – 允许来自任何语言的所有字母
-   p{N} – 用于数字
-   p{P} – 标点符号
-   p{Z} – 用于空白分隔符
-   ^表示否定，所以所有这些表达式都会被列入白名单

该表达式将只保留字母、数字、标点符号和空格。我们可以根据需要自定义表达式以允许或删除更多字符类型

我们还可以将String.replaceAll() 与相同的正则表达式一起使用：

```java
@Test
public void whenRemoveEmojiUsingRegex_thenSuccess() {
    String text = "la conférence, commencera à 10 heures ?";
    String regex = "[^p{L}p{N}p{P}p{Z}]";
    String result = text.replaceAll(regex, "");

    assertEquals(result, "la conférence, commencera à 10 heures ");
}
```

## 5.使用代码点

现在，我们还将使用代码点检测表情符号。我们可以使用x{hexidecimal value}表达式来匹配特定的 Unicode 点。

在下面的示例中，我们使用它们的 Unicode 点删除了两个 Unicode 范围的表情符号：

```java
@Test
public void whenRemoveEmojiUsingCodepoints_thenSuccess() {
    String text = "la conférence, commencera à 10 heures ?";
    String result = text.replaceAll("[x{0001f300}-x{0001f64f}]|[x{0001f680}-x{0001f6ff}]", "");

    assertEquals(result, "la conférence, commencera à 10 heures ");
}
```

[可以在此处](https://unicode.org/emoji/charts/full-emoji-list.html)找到当前可用表情符号及其代码点的完整列表。

## 6. 使用 Unicode 范围

最后，我们将再次使用 Unicode，但这次使用 u表达式。

问题是一些 Unicode 点不适合一个 16 位Java字符，因此其中一些需要两个字符。

这是使用u的相应表达式：

```java
@Test
public void whenRemoveEmojiUsingUnicode_thenSuccess() {
    String text = "la conférence, commencera à 10 heures ?";
    String result = text.replaceAll("[ud83cudf00-ud83dude4f]|[ud83dude80-ud83dudeff]", "");

    assertEquals(result, "la conférence, commencera à 10 heures ");
}
```

## 七、总结

在这篇快速文章中，我们学习了从Java字符串中删除表情符号的不同方法。我们使用了表情符号库、正则表达式和 Unicode 范围。