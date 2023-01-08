## 1. 概述

有时我们可能会遇到难以将字符串与 [正则表达式](https://www.baeldung.com/regular-expressions-java)匹配的问题。例如，我们可能不知道我们想要准确匹配什么，但我们可以知道它的周围环境，比如它前面有什么或者后面缺少什么。在这些情况下，我们可以使用环视断言。这些表达式称为断言，因为它们仅指示某项是否匹配但不包含在结果中。

在本教程中，我们将了解如何使用四种类型的正则表达式环视断言。

## 2. 积极前瞻

假设我们想分析 java 文件的导入。首先，让我们通过检查 static 关键字是否跟在import关键字之后来查找静态的 import语句。

让我们在表达式中使用(?=criteria)语法的正向先行断言来匹配主表达式导入后的静态字符组：

```java
Pattern pattern = Pattern.compile("import (?=static).+");

Matcher matcher = pattern
  .matcher("import static org.junit.jupiter.api.Assertions.assertEquals;");
assertTrue(matcher.find());
assertEquals("import static org.junit.jupiter.api.Assertions.assertEquals;", matcher.group());

assertFalse(pattern.matcher("import java.util.regex.Matcher;").find());
```

## 3.负前瞻

接下来，让我们做与上一个示例直接相反的事情，查找不是static 的import 语句。让我们通过检查static关键字没有跟在import关键字之后来做到这一点。

让我们在表达式中使用带有(?!criteria)语法的否定前瞻断言，以确保在我们的主表达式导入后静态字符组无法匹配：

```java
Pattern pattern = Pattern.compile("import (?!static).+");

Matcher matcher = pattern.matcher("import java.util.regex.Matcher;");
assertTrue(matcher.find());
assertEquals("import java.util.regex.Matcher;", matcher.group());

assertFalse(pattern
  .matcher("import static org.junit.jupiter.api.Assertions.assertEquals;").find());
```

## 4.Java中 Lookbehind 的局限性

直到Java8，我们可能会遇到这样的限制，即在回顾断言中不允许使用未绑定的量词，如+和 。也就是说，例如，以下断言将在Java8 之前抛出PatternSyntaxException ：

-   (?<!fo+)bar ，如果fo前面有一个或多个o字符，我们不想匹配bar
-   (?<!fo)bar ，如果它前面是f字符后跟零个或多个o字符，我们不想匹配bar
-   (?<!fo{2,})bar，如果foo前面有两个或更多o字符，我们不想匹配bar

作为一种解决方法，我们可以使用带有指定上限的花括号量词，例如(?<!fo{2,4})bar ，我们将f字符后面的o字符数最大化为 4。

从Java9 开始，我们可以在 lookbehinds 中使用未绑定的量词。但是，由于正则表达式实现的内存消耗，仍然建议仅在具有合理上限的后视中使用量词，例如(?<!fo{2,20})bar而不是(?<!fo{ 2,2000})栏。

## 5.积极回顾

假设我们想在分析中区分 JUnit 4 和 JUnit 5 导入。首先，让我们检查assertEquals方法的导入语句是否来自jupiter包。

让我们在表达式中使用带有(?<=criteria)语法的正后向断言来匹配 主表达式 . assertEquals之前的字符组jupiter：

```java
Pattern pattern = Pattern.compile(".(?<=jupiter).assertEquals;");

Matcher matcher = pattern
  .matcher("import static org.junit.jupiter.api.Assertions.assertEquals;");
assertTrue(matcher.find());
assertEquals("import static org.junit.jupiter.api.Assertions.assertEquals;", matcher.group());

assertFalse(pattern.matcher("import static org.junit.Assert.assertEquals;").find());
```

## 6. 负面回顾

接下来，让我们做与上一个示例直接相反的事情，查找不是来自jupiter包的导入语句。

为此，让我们在表达式中使用带有(?<!criteria)语法的否定后向断言，以确保字符组jupiter.{0,30}不能在我们的主表达式assertEquals之前匹配：

```java
Pattern pattern = Pattern.compile(".(?<!jupiter.{0,30})assertEquals;");

Matcher matcher = pattern.matcher("import static org.junit.Assert.assertEquals;");
assertTrue(matcher.find());
assertEquals("import static org.junit.Assert.assertEquals;", matcher.group());

assertFalse(pattern
  .matcher("import static org.junit.jupiter.api.Assertions.assertEquals;").find());
```

## 七、总结

在本文中，我们了解了如何使用四种类型的正则表达式环视来解决一些用正则表达式匹配字符串的困难情况。