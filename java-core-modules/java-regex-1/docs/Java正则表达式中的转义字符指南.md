## 一、概述

Java 中的正则表达式 API，java.util.regex被广泛用于模式匹配。要了解更多，你可以关注这篇[文章](https://www.baeldung.com/regular-expressions-java)。

在本文中，我们将专注于使用正则表达式转义字符，并展示如何在 Java 中完成。

## 2.特殊正则表达式字符

根据 Java 正则表达式 API 文档，正则表达式中存在一组特殊字符，也称为元字符。

当我们想要按原样允许字符而不是用它们的特殊含义来解释它们时，我们需要对它们进行转义。通过转义这些字符，我们强制将字符串与给定的正则表达式匹配时将它们视为普通字符。

我们通常需要通过这种方式转义的元字符是：

<([{^-=$!|]})?+.>

让我们看一个简单的代码示例，我们将输入字符串与以正则表达式表示的模式相匹配。

这个测试表明对于给定的输入字符串foof当模式foo时。( foo以点字符结尾) 被匹配，则返回值为true，表示匹配成功。

```java
@Test
public void givenRegexWithDot_whenMatchingStr_thenMatches() {
    String strInput = "foof";
    String strRegex = "foo.";
      
    assertEquals(true, strInput.matches(strRegex));
}
```

你可能想知道为什么在输入字符串中没有点 (.) 字符时匹配成功？

答案很简单。点(.)是一个元字符——这里点的特殊意义在于它的位置可以是“任何字符”。因此，很明显匹配器是如何确定找到匹配项的。

假设我们不想以其独特的含义对待点 (.) 字符。相反，我们希望它被解释为一个点符号。这意味着在前面的例子中，我们不想让模式foo. 在输入字符串中匹配。

我们将如何处理这样的情况？答案是：我们需要转义点 (.) 字符，以便忽略它的特殊含义。

让我们在下一节中更详细地研究它。

## 3.转义字符

根据正则表达式的 Java API 文档，有两种方法可以转义具有特殊含义的字符。换句话说，就是强行把他们当成普通人物对待。

让我们看看它们是什么：

1.  在元字符前加上反斜杠 ()
2.  用Q和E括起一个元字符

这只是意味着在我们之前看到的例子中，如果我们想要转义点字符，我们需要在点字符之前放置一个反斜杠字符。或者，我们可以将点字符放在 Q 和 E 之间。

### 3.1. 使用反斜杠转义

这是我们可以用来转义正则表达式中的元字符的技术之一。但是，我们知道反斜杠字符也是 Java String文字中的转义字符。因此，在任何字符(包括  字符本身)之前使用它时，我们需要将反斜杠字符加倍。

因此，在我们的示例中，我们需要更改此测试中所示的正则表达式：

```java
@Test
public void givenRegexWithDotEsc_whenMatchingStr_thenNotMatching() {
    String strInput = "foof";
    String strRegex = "foo.";

    assertEquals(false, strInput.matches(strRegex));
}
```

在这里，点字符被转义，因此匹配器只是将其视为一个点并尝试找到以点结尾的模式(即foo.)。

在这种情况下，它返回false ，因为在该模式的输入字符串中没有匹配项。

### 3.2. 使用 Q & E 转义

或者，我们可以使用Q和E来转义特殊字符。Q表示直到E 的所有字符都需要转义，而E意味着我们需要结束以Q开始的转义。

这只是意味着Q和E之间的任何内容都将被转义。

在此处显示的测试中，String类的split()使用提供给它的正则表达式进行匹配。

我们的需求是将输入的字符串通过竖线(|)字符拆分成单词。因此，我们使用正则表达式模式来这样做。

管道符是正则表达式中需要转义的元字符。

在这里，转义是通过将管道字符放在Q和E之间完成的：

```java
@Test
public void givenRegexWithPipeEscaped_whenSplitStr_thenSplits() {
    String strInput = "foo|bar|hello|world";
    String strRegex = "Q|E";
    
    assertEquals(4, strInput.split(strRegex).length);
}
```

## 4. Pattern.quote(String S) 方法

java.util.regex.Pattern类中的 Pattern.Quote(String S) 方法将给定的正则表达式模式String转换为文字模式String。这意味着输入字符串中的所有元字符都被视为普通字符。

使用此方法比使用Q & E更方便，因为它用它们包装给定的String 。

让我们看看这个方法的实际效果：

```java
@Test
public void givenRegexWithPipeEscQuoteMeth_whenSplitStr_thenSplits() {
    String strInput = "foo|bar|hello|world";
    String strRegex = "|";

    assertEquals(4,strInput.split(Pattern.quote(strRegex)).length);
}
```

在此快速测试中，Pattern.quote()方法用于转义给定的正则表达式模式并将其转换为字符串文字。换句话说，它为我们转义了正则表达式模式中存在的所有元字符。它正在做与Q & E类似的工作。

管道字符由Pattern.quote()方法进行转义，split()将其解释为字符串文字，并据以分割输入。

正如我们所见，这是一种更简洁的方法，而且开发人员不必记住所有的转义序列。

我们应该注意到Pattern.quote将整个块包含在一个转义序列中。如果我们想单独转义字符，我们需要使用[令牌替换算法](https://www.baeldung.com/java-regex-token-replacement)。

## 5. 附加示例

让我们看看java.util.regex.Matcher的replaceAll()方法是如何工作的。

如果我们需要用另一个字符串替换所有出现的给定字符串，我们可以通过将正则表达式传递给它来使用此方法。

想象一下，我们有一个多次出现$字符的输入。我们想要得到的结果是$字符被 £ 替换的相同字符串。

此测试演示了模式$如何在不转义的情况下通过：

```java
@Test
public void givenRegexWithDollar_whenReplacing_thenNotReplace() {
 
    String strInput = "I gave $50 to my brother."
      + "He bought candy for $35. Now he has $15 left.";
    String strRegex = "$";
    String strReplacement = "£";
    String output = "I gave £50 to my brother."
      + "He bought candy for £35. Now he has £15 left.";
    
    Pattern p = Pattern.compile(strRegex);
    Matcher m = p.matcher(strInput);
        
    assertThat(output, not(equalTo(m.replaceAll(strReplacement))));
}
```

该测试断言$没有被£正确替换。

现在，如果我们转义正则表达式模式，替换就会正确发生，并且测试会通过，如以下代码片段所示：

```java
@Test
public void givenRegexWithDollarEsc_whenReplacing_thenReplace() {
 
    String strInput = "I gave $50 to my brother."
      + "He bought candy for $35. Now he has $15 left.";
    String strRegex = "$";
    String strReplacement = "£";
    String output = "I gave £50 to my brother."
      + "He bought candy for £35. Now he has £15 left.";
    Pattern p = Pattern.compile(strRegex);
    Matcher m = p.matcher(strInput);
    
    assertEquals(output,m.replaceAll(strReplacement));
}
```

注意这里的$ ，它通过转义$字符并成功匹配模式来达到目的。

## 六，总结

在本文中，我们研究了 Java 正则表达式中的转义字符。

我们讨论了为什么需要转义正则表达式，以及实现转义的不同方法。