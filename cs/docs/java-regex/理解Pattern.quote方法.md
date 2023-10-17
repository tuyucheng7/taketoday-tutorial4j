## 1. 概述

在Java中使用正则表达式时，有时我们需要以文字形式匹配正则表达式模式——而不处理这些序列中存在的任何[元字符](https://www.baeldung.com/regular-expressions-java#Characters)。

在本快速教程中，让我们看看如何手动和使用Java 提供的[Pattern.quote()方法转义正则表达式中的元字符。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#quote(java.lang.String))

## 2. 不转义元字符

让我们考虑一个包含美元金额列表的字符串：

```java
String dollarAmounts = "$100.25, $100.50, $150.50, $100.50, $100.75";
```

现在，假设我们需要在其中搜索特定金额的美元。让我们相应地初始化一个正则表达式模式字符串：

```
String patternStr = "$100.50";
```

首先，让我们看看如果我们在不转义任何元字符的情况下执行正则表达式搜索会发生什么：

```java
public void whenMetacharactersNotEscaped_thenNoMatchesFound() {
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(dollarAmounts);

    int matches = 0;
    while (matcher.find()) {
        matches++;
    }

    assertEquals(0, matches);
}
```

正如我们所见，匹配器在我们的dollarAmounts字符串中甚至找不到一次$150.50。这仅仅是因为以美元符号开头的patternStr恰好是[指定行尾的](https://docs.oracle.com/javase/tutorial/essential/regex/bounds.html#PageTitle)[正则表达式](https://docs.oracle.com/javase/tutorial/essential/regex/bounds.html#PageTitle)元字符。

正如你可能已经猜到的那样，我们会在所有正则表达式元字符上面临同样的问题。我们将无法搜索包含指数插入符 (^) 的数学语句，例如“ 5^3 ”，或使用反斜杠 () 的文本，例如“ usersbob ”。

## 3.手动忽略元字符

其次，让我们在执行搜索之前转义正则表达式中的元字符：

```java
public void whenMetacharactersManuallyEscaped_thenMatchingSuccessful() {
    String metaEscapedPatternStr = "Q" + patternStr + "E";
    Pattern pattern = Pattern.compile(metaEscapedPatternStr);
    Matcher matcher = pattern.matcher(dollarAmounts);

    int matches = 0;
    while (matcher.find()) {
        matches++;
    }

    assertEquals(2, matches);
}
```

这一次，我们成功地进行了搜索；但由于以下几个原因，这不是理想的解决方案：

-   转义使代码更难理解的元字符时执行的字符串连接。
-   由于添加了硬编码值，代码不太干净。

## 4. 使用Pattern.quote()

最后，让我们看看在正则表达式中忽略元字符的最简单和最干净的方法。

Java[在其](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#quote(java.lang.String))[Pattern](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#quote(java.lang.String))[类中提供了一个](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#quote(java.lang.String))[quote()方法](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#quote(java.lang.String))来检索字符串的文字模式：

```java
public void whenMetacharactersEscapedUsingPatternQuote_thenMatchingSuccessful() {
    String literalPatternStr = Pattern.quote(patternStr);
    Pattern pattern = Pattern.compile(literalPatternStr);
    Matcher matcher = pattern.matcher(dollarAmounts);

    int matches = 0;
    while (matcher.find()) {
        matches++;
    }

    assertEquals(2, matches);
}
```

## 5.总结

在本文中，我们研究了如何处理字面形式的正则表达式模式。

我们看到了不转义正则表达式元字符如何无法提供预期的结果，以及如何使用Pattern.quote()方法手动执行正则表达式模式中的转义元字符。