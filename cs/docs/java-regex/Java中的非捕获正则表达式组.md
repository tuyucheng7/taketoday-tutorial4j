## 1. 概述

非捕获组是[Java Regular Expressions](https://www.baeldung.com/regular-expressions-java)中的重要结构。他们创建了一个子模式，该模式作为一个单元运行，但不保存匹配的字符序列。在本教程中，我们将探讨如何在Java正则表达式中使用非捕获组。

## 2.正则表达式组

正则表达式组可以是以下两种类型之一：捕获和非捕获。

捕获组保存匹配的字符序列。它们的值可以用作模式中的反向引用和/或稍后在代码中检索。

尽管它们不保存匹配的字符序列，但非捕获组可以更改组内的模式匹配修饰符。一些非捕获组甚至可以在成功的子模式匹配后丢弃回溯信息。

让我们探索一些非捕获组的例子。

## 3.非捕获组

使用运算符“ (?:X) ”创建非捕获组。“ X ”是组的模式：

```java
Pattern.compile("[^:]+://(?:[.a-z]+/?)+")
```

此模式有一个非捕获组。如果它类似于 URL，它将匹配一个值。URL 的完整正则表达式会涉及更多。我们使用一个简单的模式来关注非捕获组。

模式“[^:]: ”匹配协议——例如，“ http:// ”。非捕获组“ (?:[.az]+/?) ”匹配带有可选斜杠的域名。由于“ + ”运算符匹配此模式的一次或多次出现，因此我们也将匹配后续路径段。让我们在 URL 上测试这个模式：

```java
Pattern simpleUrlPattern = Pattern.compile("[^:]+://(?:[.a-z]+/?)+");
Matcher urlMatcher
  = simpleUrlPattern.matcher("http://www.microsoft.com/some/other/url/path");
    
Assertions.assertThat(urlMatcher.matches()).isTrue();

```

让我们看看当我们尝试检索匹配的文本时会发生什么：

```java
Pattern simpleUrlPattern = Pattern.compile("[^:]+://(?:[.a-z]+/?)+");
Matcher urlMatcher = simpleUrlPattern.matcher("http://www.microsoft.com/");
    
Assertions.assertThat(urlMatcher.matches()).isTrue();
Assertions.assertThatThrownBy(() -> urlMatcher.group(1))
  .isInstanceOf(IndexOutOfBoundsException.class);
```

正则表达式被编译成java.util.Pattern对象。然后，我们创建一个java.util.Matcher以将我们的模式应用于提供的值。

[接下来，我们断言matches()](https://www.baeldung.com/java-matcher-find-vs-matches)的结果返回true。

我们使用了一个非捕获组来匹配 URL 中的域名。由于非捕获组不保存匹配的文本，我们无法检索匹配的文本“www.microsoft.com/”。尝试检索域名将导致IndexOutOfBoundsException。

### 3.1. 行内修饰符

正则表达式区分大小写。如果我们将我们的模式应用于大小写混合的 URL，匹配将失败：

```java
Pattern simpleUrlPattern
  = Pattern.compile("[^:]+://(?:[.a-z]+/?)+");
Matcher urlMatcher
  = simpleUrlPattern.matcher("http://www.Microsoft.com/");
    
Assertions.assertThat(urlMatcher.matches()).isFalse();
```

在我们也想匹配大写字母的情况下，我们可以尝试几个选项。

一种选择是将大写字符范围添加到模式中：

```java
Pattern.compile("[^:]+://(?:[.a-zA-Z]+/?)+")
```

另一种选择是使用修饰符标志。因此，我们可以将正则表达式编译为不区分大小写：

```java
Pattern.compile("[^:]+://(?:[.a-z]+/?)+", Pattern.CASE_INSENSITIVE)
```

非捕获组允许第三种选择：我们可以只更改组的修饰符标志。让我们将不区分大小写的修饰符标志(“ i ”)添加到组中：

```java
Pattern.compile("[^:]+://(?i:[.a-z]+/?)+");
```

现在我们已经使组不区分大小写，让我们将此模式应用于混合大小写的 URL：

```java
Pattern scopedCaseInsensitiveUrlPattern
  = Pattern.compile("[^:]+://(?i:[.a-z]+/?)+");
Matcher urlMatcher
  = scopedCaseInsensitiveUrlPattern.matcher("http://www.Microsoft.com/");
    
Assertions.assertThat(urlMatcher.matches()).isTrue();
```

当模式被编译为不区分大小写时，我们可以通过在修饰符前添加“-”运算符来关闭它。让我们将此模式应用于另一个大小写混合的 URL：

```java
Pattern scopedCaseSensitiveUrlPattern
  = Pattern.compile("[^:]+://(?-i:[.a-z]+/?)+/ending-path", Pattern.CASE_INSENSITIVE);
Matcher urlMatcher
  = scopedCaseSensitiveUrlPattern.matcher("http://www.Microsoft.com/ending-path");
  
Assertions.assertThat(urlMatcher.matches()).isFalse();

```

在此示例中，最终路径段“ /ending-path ”不区分大小写。模式的“ /ending-path ”部分将匹配大写和小写字符。

当我们关闭组内的不区分大小写选项时，非捕获组只支持小写字符。因此，大小写混合的域名不匹配。

## 4.独立的非捕获组

独立的非捕获组是一种正则表达式组。这些组在找到成功匹配后丢弃回溯信息。使用这种类型的组时，我们需要注意何时会发生回溯。否则，我们的模式可能与我们认为应该的值不匹配。

[回溯](https://www.baeldung.com/java-regex-performance)是非确定性有限自动机 (NFA) 正则表达式引擎的一项功能。当引擎无法匹配文本时，NFA 引擎可以探索模式中的备选方案。在用尽所有可用的替代方案后，引擎将无法匹配。我们只介绍回溯，因为它与独立的非捕获组有关。

使用运算符“ (?>X) ”创建一个独立的非捕获组，其中X是子模式：

```
Pattern.compile("[^:]+://(?>[.a-z]+/?)+/ending-path");
```

我们添加了“ /ending-path ”作为常量路径段。有这个额外的要求会迫使回溯情况。域名和其他路径段可以匹配斜杠字符。为了匹配“/ending-path”，引擎需要回溯。通过回溯，引擎可以从组中删除斜杠并将其应用于模式的“ /ending-path ”部分。

让我们将独立的非捕获组模式应用于 URL：

```java
Pattern independentUrlPattern
  = Pattern.compile("[^:]+://(?>[.a-z]+/?)+/ending-path");
Matcher independentMatcher
  = independentUrlPattern.matcher("http://www.microsoft.com/ending-path");
    
Assertions.assertThat(independentMatcher.matches()).isFalse();
```

该组成功匹配域名和斜线。所以，我们离开独立非捕获组的范围。

此模式需要在“ending-path”之前出现一个斜杠。但是，我们的独立非捕获组匹配到了斜线。

NFA 引擎应该尝试回溯。由于组末尾的斜线是可选的，NFA 引擎将从组中删除斜线并重试。独立非捕获组丢弃了回溯信息。因此，NFA 引擎无法回溯。

### 4.1. 组内回溯

回溯可以发生在一个独立的非捕获组中。NFA引擎在匹配组的同时，回溯信息并没有被丢弃。直到组匹配成功后，回溯信息才被丢弃：

```java
Pattern independentUrlPatternWithBacktracking
  = Pattern.compile("[^:]+://(?>(?:[.a-z]+/?)+/)ending-path");
Matcher independentMatcher
  = independentUrlPatternWithBacktracking.matcher("http://www.microsoft.com/ending-path");
    
Assertions.assertThat(independentMatcher.matches()).isTrue();
```

现在我们在一个独立的非捕获组中有一个非捕获组。我们还有一个回溯的情况，涉及到“ending-path”前面的斜线。但是，我们将模式的回溯部分包含在独立的非捕获组中。回溯将发生在独立的非捕获组中。因此 NFA 引擎有足够的信息进行回溯，并且模式与提供的 URL 相匹配。

## 5.总结

我们已经展示了非捕获组与捕获组不同。然而，它们的功能就像它们的捕获对手一样是一个整体。我们还展示了非捕获组可以启用或禁用组的修饰符，而不是整个模式。

同样，我们已经展示了独立的非捕获组如何丢弃回溯信息。没有这些信息，NFA 引擎就无法探索替代方案来进行成功匹配。但是，回溯可能发生在组内。