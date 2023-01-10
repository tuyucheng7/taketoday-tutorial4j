## 1. 概述

简而言之，Apache Commons Text 库包含许多有用的实用方法来处理字符串，这超出了核心Java提供的功能。

在此快速介绍中，我们将了解 Apache Commons Text 是什么、它的用途以及使用该库的一些实际示例。

## 2.Maven依赖

让我们首先将以下 Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.10</version>
</dependency>
```

[可以在Maven Central Repository](https://mvnrepository.com/artifact/org.apache.commons/commons-text)找到最新版本的库。

## 三、概述

根包org.apache.commons.text分为不同的子包：

-   org.apache.commons.text.diff –字符串之间的差异
-   org.apache.commons.text.similarity –字符串之间的相似性和距离
-   org.apache.commons.text.translate – 翻译文本

让我们更详细地了解每个包的用途。

## 3. 处理文本

org.apache.commons.text包包含多个用于处理字符串的工具。

例如，WordUtils的 API 能够将字符串中每个单词的首字母大写，交换字符串的大小写，并检查字符串是否包含给定数组中的所有单词。

让我们看看如何将字符串中每个单词的首字母大写：

```java
@Test
public void whenCapitalized_thenCorrect() {
    String toBeCapitalized = "to be capitalized!";
    String result = WordUtils.capitalize(toBeCapitalized);
    
    assertEquals("To Be Capitalized!", result);
}
```

以下是我们如何检查字符串是否包含数组中的所有单词：

```java
@Test
public void whenContainsWords_thenCorrect() {
    boolean containsWords = WordUtils
      .containsAllWords("String to search", "to", "search");
    
    assertTrue(containsWords);
}
```

StrSubstitutor提供了一种从模板构建字符串的便捷方式：

```java
@Test
public void whenSubstituted_thenCorrect() {
    Map<String, String> substitutes = new HashMap<>();
    substitutes.put("name", "John");
    substitutes.put("college", "University of Stanford");
    String templateString = "My name is ${name} and I am a student at the ${college}.";
    StrSubstitutor sub = new StrSubstitutor(substitutes);
    String result = sub.replace(templateString);
    
    assertEquals("My name is John and I am a student at the University of Stanford.", result);
}
```

StrBuilder是Java.lang.StringBuilder的替代品。它提供了一些StringBuilder没有提供的新特性。

例如，我们可以用另一个String 替换所有出现的 String或清除String而无需将新对象分配给它的引用。

这是一个替换字符串的一部分的快速示例：

```java
@Test
public void whenReplaced_thenCorrect() {
    StrBuilder strBuilder = new StrBuilder("example StrBuilder!");
    strBuilder.replaceAll("example", "new");
   
    assertEquals(new StrBuilder("new StrBuilder!"), strBuilder);
}
```

要清除一个字符串，我们可以简单地通过调用构建器上的clear()方法来完成：

```java
strBuilder.clear();
```

## 4.计算字符串之间的差异

包org.apache.commons.text.diff实现了 Myers 算法来计算两个字符串之间的差异。

两个字符串之间的差异由可以将一个字符串转换为另一个字符串的一系列修改定义。

可使用三种类型的命令将字符串转换为另一个字符串——InsertCommand、KeepCommand和DeleteCommand。

一个EditScript对象包含应该运行的脚本，以便将一个字符串转换为另一个字符串。让我们计算为了将一个字符串转换为另一个字符串而应该进行的单个字符修改的数量：

```java
@Test
public void whenEditScript_thenCorrect() {
    StringsComparator cmp = new StringsComparator("ABCFGH", "BCDEFG");
    EditScript<Character> script = cmp.getScript();
    int mod = script.getModifications();
    
    assertEquals(4, mod);
}
```

## 5.字符串之间的异同

org.apache.commons.text.similarity包包含可用于查找字符串之间的相似性和距离的算法。

例如，LongestCommonSubsequence可用于查找两个字符串中常见字符的数量：

```java
@Test
public void whenCompare_thenCorrect() {
    LongestCommonSubsequence lcs = new LongestCommonSubsequence();
    int countLcs = lcs.apply("New York", "New Hampshire");
    
    assertEquals(5, countLcs);
}
```

同样，LongestCommonSubsequenceDistance可用于查找两个字符串中不同字符的数量：

```java
@Test
public void whenCalculateDistance_thenCorrect() {
    LongestCommonSubsequenceDistance lcsd = new LongestCommonSubsequenceDistance();
    int countLcsd = lcsd.apply("New York", "New Hampshire");
    
    assertEquals(11, countLcsd);
}
```

## 6. 文字翻译

最初创建org.apache.text.translate包是为了允许我们自定义StringEscapeUtils提供的规则。

该包有一组类负责将文本转换为一些不同的字符编码模型，例如 Unicode 和数字字符参考。我们还可以创建自己的定制翻译例程。

让我们看看如何将String转换为其等效的 Unicode 文本：

```java
@Test
public void whenTranslate_thenCorrect() {
    UnicodeEscaper ue = UnicodeEscaper.above(0);
    String result = ue.translate("ABCD");
    
    assertEquals("u0041u0042u0043u0044", result);
}
```

在这里，我们将要开始翻译的字符的索引传递给above()方法。

LookupTranslator使我们能够定义我们自己的查找表，其中每个字符都可以有一个对应的值，并且我们可以将任何文本翻译成其对应的等价物。

## 七. 总结

在本快速教程中，我们概述了 Apache Commons Text 的全部内容及其一些常见功能。