## 1. 概述

在本教程中，我们将简要介绍使用[Jsoup](https://www.baeldung.com/java-with-jsoup)将 HTML 解析为纯文本时保留换行符的不同方法。我们将介绍如何保留与换行符 ( n ) 相关的换行符，以及与<br>和<p>标签相关的换行符。

## 2.解析 HTML 文本时保留n

Jsoup默认从 HTML 文本中删除换行符 ( n )，并将每个换行符替换为空格字符。

但是，为了防止 Jsoup 删除换行符，我们可以更改 Jsoup 的[OutputSetting](https://jsoup.org/apidocs/org/jsoup/nodes/Document.OutputSettings.html#prettyPrint(boolean))并禁用 pretty-print。如果 pretty-print 被禁用，HTML 输出方法将不会重新格式化输出，并且输出看起来像输入：

```java
Document.OutputSettings outputSettings = new Document.OutputSettings();
outputSettings.prettyPrint(false);
```

此外，我们可以使用[Jsoup #clean从](https://jsoup.org/apidocs/org/jsoup/Jsoup.html#clean(java.lang.String,java.lang.String,org.jsoup.safety.Whitelist,org.jsoup.nodes.Document.OutputSettings)) 字符串中删除所有 HTML 标记[：](https://jsoup.org/apidocs/org/jsoup/Jsoup.html#clean(java.lang.String,java.lang.String,org.jsoup.safety.Whitelist,org.jsoup.nodes.Document.OutputSettings))

```java
String strHTML = "<html><body>Hellonworld</body></html>";
String strWithNewLines = Jsoup.clean(strHTML, "", Whitelist.none(), outputSettings);
```

让我们看看我们的输出字符串strWithNewLines是什么样子的：

```java
assertEquals("Hellonworld", strWithNewLines);
```

因此，我们可以看到，通过使用Whitelist #none调用Jsoup #clean并禁用Jsoup的漂亮打印输出设置，我们能够保留与换行符相关的换行符。

让我们看看我们还能做什么！

## 3. 保留与<br>和<p>标签相关的换行符

在使用Jsoup #clean方法清理HTML 文本时，它会删除由<br>和<p>等 HTML 标记创建的换行符。

为了保留与这些标签关联的换行符，我们首先需要从我们的 HTML 字符串创建一个[Jsoup文档：](https://jsoup.org/apidocs/org/jsoup/nodes/Document.html)

```java
String strHTML = "<html><body>Hello<br>World<p>Paragraph</p></body></html>";
Document jsoupDoc = Jsoup.parse(strHTML);
```

接下来，我们在<br>和<p>标签之前添加一个换行符——同样，我们也禁用了漂亮的打印输出设置：

```java
Document.OutputSettings outputSettings = new Document.OutputSettings();
outputSettings.prettyPrint(false);
jsoupDoc.outputSettings(outputSettings);
jsoupDoc.select("br").before("n");
jsoupDoc.select("p").before("n");
```

在这里，我们使用了Jsoup Document的[select](https://www.baeldung.com/java-with-jsoup#1-selecting)方法和before方法来前置换行符。

之后，我们从jsoupDoc中获取保留原始新行的 HTML 字符串：

```java
String str = jsoupDoc.html().replaceAll("n", "n");
```

最后，我们调用Jsoup #clean with Whitelist #none并禁用pretty -print 输出设置：

```java
String strWithNewLines = Jsoup.clean(str, "", Whitelist.none(), outputSettings);
```

我们的输出字符串strWithNewLines看起来像：

```java
assertEquals("HellonWorldnParagraph", strWithNewLines);
```

因此，通过在<br>和<p> HTML 标记前加上换行符，并禁用 Jsoup 的漂亮打印输出设置，我们可以保留与它们相关的换行符。

## 4. 总结

 在这篇简短的文章中，我们学习了如何在使用 Jsoup 将 HTML 解析为纯文本时保留与换行符 ( n ) 以及<br>和<p>标记关联的换行符。