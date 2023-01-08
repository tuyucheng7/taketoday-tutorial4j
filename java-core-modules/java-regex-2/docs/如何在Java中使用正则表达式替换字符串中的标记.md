## 1. 概述

当我们需要在Java中查找或替换字符串中的值时，通常会使用[正则表达式](https://www.baeldung.com/regular-expressions-java)。这些使我们能够确定[字符串的部分或全部](https://www.baeldung.com/java-matcher-find-vs-matches)是否与模式匹配。我们可以使用Matcher和String中的 replaceAll方法轻松地将相同的替换应用于字符串中的多个标记。

在本教程中，我们将探索如何为字符串中找到的每个标记应用不同的替换。这将使我们更容易满足转义某些字符或替换占位符值等用例。

我们还将了解一些调整正则表达式以正确识别标记的技巧。

## 2. 单独处理匹配

在我们构建逐个令牌替换算法之前，我们需要了解围绕正则表达式的JavaAPI。让我们使用捕获和非捕获组来解决一个棘手的匹配问题。

### 2.1. 标题案例示例

假设我们想要构建一个算法来处理字符串中的所有标题词。这些单词以一个大写字符开头，然后以小写字符结尾或继续。

我们的输入可能是：

```java
"First 3 Capital Words! then 10 TLAs, I Found"
```

根据标题词的定义，这包含匹配项：

-   第一的
-   首都
-   字
-   我
-   成立

识别这种模式的正则表达式是：

```java
"(?<=^|[^A-Za-z])([A-Z][a-z])(?=[^A-Za-z]|$)"
```

为了理解这一点，让我们把它分解成它的组成部分。我们将从中间开始：

```java
[A-Z]
```

将识别单个大写字母。

我们允许单字符单词或后跟小写字母的单词，因此：

```java
[a-z]
```

识别零个或多个小写字母。

在某些情况下，上述两个字符类就足以识别我们的标记。不幸的是，在我们的示例文本中，有一个单词以多个大写字母开头。因此，我们需要表达，我们找到的单个大写字母一定是在非字母之后最先出现的。

类似地，由于我们允许单个大写字母单词，因此我们需要表达我们找到的单个大写字母一定不能是多个大写字母单词的第一个。

表达式[^A-Za-z] 的 意思是“没有字母”。我们将其中之一放在非捕获组中表达式的开头：

```java
(?<=^|[^A-Za-z])
```

以(?<=开头的非捕获组进行后视以确保匹配项出现在正确的边界。其末尾的对应项对后面的字符执行相同的工作。

但是，如果单词触及字符串的开头或结尾，那么我们需要考虑到这一点，这是我们添加 ^| 的地方 到第一组，使其表示“字符串的开头或任何非字母字符”，并且我们在最后一个非捕获组的末尾添加了 |$ 以允许字符串的末尾作为边界.

当我们使用 find时，在非捕获组中找到的字符不会出现在匹配中。

我们应该注意到，即使像这样的简单用例也可能有很多边缘情况，因此测试我们的正则表达式很重要。为此，我们可以编写单元测试，使用我们的 IDE 的内置工具，或使用像[Regexr](https://regexr.com/4vdaf)这样的在线工具。

### 2.2. 测试我们的例子

使用名为EXAMPLE_INPUT的常量中的示例文本和名为 TITLE_CASE_PATTERN的模式中的正则表达式 ，让我们在Matcher类上使用find来提取单元测试中的所有匹配项：

```java
Matcher matcher = TITLE_CASE_PATTERN.matcher(EXAMPLE_INPUT);
List<String> matches = new ArrayList<>();
while (matcher.find()) {
    matches.add(matcher.group(1));
}

assertThat(matches)
  .containsExactly("First", "Capital", "Words", "I", "Found");
```

这里我们使用Pattern上的 matcher函数来生成一个 Matcher。然后我们在循环中 使用find方法，直到它停止返回true 以迭代所有匹配项。

每次 find返回true时， Matcher对象的状态都设置为表示当前匹配项。我们可以使用group(0) 检查整个匹配项 ，或者使用基于 1 的索引检查特定的捕获组。在这种情况下，我们想要的片段周围有一个捕获组，因此我们使用group(1)将匹配项添加到我们的列表中。

### 2.3. 更多地检查匹配器

到目前为止，我们已经成功地找到了我们想要处理的词。

但是，如果这些单词中的每一个都是我们想要替换的标记，我们将需要了解有关匹配的更多信息才能构建结果字符串。让我们看看 Matcher的其他一些可能对我们有帮助的属性：

```java
while (matcher.find()) {
    System.out.println("Match: " + matcher.group(0));
    System.out.println("Start: " + matcher.start());
    System.out.println("End: " + matcher.end());
}
```

此代码将向我们显示每个匹配项的位置。它还向我们展示了group(0)匹配，这是捕获的所有内容：

```java
Match: First
Start: 0
End: 5
Match: Capital
Start: 8
End: 15
Match: Words
Start: 16
End: 21
Match: I
Start: 37
End: 38
... more
```

在这里我们可以看到每个匹配项只包含我们期望的单词。start 属性显示字符串中匹配的从零开始的索引。末尾 显示紧随其后的字符的索引。这意味着我们可以使用substring(start, end-start)从原始字符串中提取每个匹配项。这基本上就是group方法为我们做的。

现在我们可以使用find来迭代匹配项，让我们处理我们的标记。

## 3. 一场一场的替换比赛

让我们继续我们的示例，使用我们的算法将原始字符串中的每个标题词替换为对应的小写字母。这意味着我们的测试字符串将被转换为：

```plaintext
"first 3 capital words! then 10 TLAs, i found"
```

Pattern 和 Matcher类不能为我们做这些，所以我们需要构造一个算法。

### 3.1. 替换算法

下面是该算法的伪代码：

-   从空输出字符串开始
-   对于每场比赛：
    -   将匹配之前和之前任何匹配之后的任何内容添加到输出中
    -   处理这个匹配并将其添加到输出
    -   继续直到处理完所有匹配项
    -   将最后一场比赛之后剩下的任何东西添加到输出中

我们应该注意到，该算法的目的是找到所有未匹配的区域并将它们添加到输出中，以及添加处理过的匹配项。

### 3.2.Java中的令牌替换器

我们想把每个单词都转成小写，所以可以写一个简单的转换方法：

```java
private static String convert(String token) {
    return token.toLowerCase();
}
```

现在我们可以编写算法来迭代匹配项。这可以使用StringBuilder作为输出：

```java
int lastIndex = 0;
StringBuilder output = new StringBuilder();
Matcher matcher = TITLE_CASE_PATTERN.matcher(original);
while (matcher.find()) {
    output.append(original, lastIndex, matcher.start())
      .append(convert(matcher.group(1)));

    lastIndex = matcher.end();
}
if (lastIndex < original.length()) {
    output.append(original, lastIndex, original.length());
}
return output.toString();
```

我们应该注意到StringBuilder提供了一个方便的append版本 ，可以提取子字符串。这与Matcher的 end属性配合得很好， 让我们可以拾取自上次匹配以来所有未匹配的字符。

## 4. 推广算法

既然我们已经解决了替换某些特定令牌的问题，为什么我们不将代码转换为可以用于一般情况的形式呢？不同实现之间唯一不同的是要使用的正则表达式，以及将每个匹配项转换为替换项的逻辑。

### 4.1. 使用函数和模式输入

我们可以使用JavaFunction<Matcher, String>对象来允许调用者提供处理每个匹配项的逻辑。我们可以采用名为tokenPattern 的输入来查找所有标记：

```java
// same as before
while (matcher.find()) {
    output.append(original, lastIndex, matcher.start())
      .append(converter.apply(matcher));

// same as before
```

在这里，正则表达式不再是硬编码的。相反，转换器函数由调用者提供，并应用于查找循环中的每个匹配项。

### 4.2. 测试通用版本

让我们看看通用方法是否与原始方法一样有效：

```java
assertThat(replaceTokens("First 3 Capital Words! then 10 TLAs, I Found",
  TITLE_CASE_PATTERN,
  match -> match.group(1).toLowerCase()))
  .isEqualTo("first 3 capital words! then 10 TLAs, i found");
```

在这里我们看到调用代码很简单。转换函数很容易表示为 lambda。测试通过。

现在我们有一个令牌替换器，所以让我们尝试一些其他用例。

## 5.一些用例

### 5.1. 转义特殊字符

假设我们想使用正则表达式转义字符手动引用正则表达式的每个字符，而不是[使用 quote方法](https://www.baeldung.com/java-regexp-escape-char)。也许我们引用一个字符串作为创建正则表达式以传递给另一个库或服务的一部分，所以块引用表达式是不够的。

如果我们可以表达表示“正则表达式字符”的模式，则很容易使用我们的算法将它们全部转义：

```java
Pattern regexCharacters = Pattern.compile("[<([{^-=$!|]})?+.>]");

assertThat(replaceTokens("A regex character like [",
  regexCharacters,
  match -> "" + match.group()))
  .isEqualTo("A regex character like [");
```

对于每个匹配项，我们都在前缀字符。由于 是Java字符串中的特殊字符，因此它被另一个转义。

实际上，此示例包含额外的 字符，因为regexCharacters模式中的字符类必须引用许多特殊字符。这显示了我们使用它们来表示它们的文字而不是正则表达式语法的正则表达式解析器。

### 5.2. 替换占位符

表示占位符的常用方法是使用类似 ${name}的语法。让我们考虑一个用例，其中需要从名为placeholderValues的映射中填充 模板“Hi ${name} at ${company}” ：

```java
Map<String, String> placeholderValues = new HashMap<>();
placeholderValues.put("name", "Bill");
placeholderValues.put("company", "Baeldung");
```

我们只需要一个好的正则表达式来查找 ${...}标记：

```java
"${(?<placeholder>[A-Za-z0-9-_]+)}"
```

是一种选择。它必须引用 $和初始花括号，否则它们将被视为正则表达式语法。

该模式的核心是占位符名称的捕获组。我们使用了一个允许字母数字、破折号和下划线的字符类，这应该适合大多数用例。

但是，为了使代码更具可读性，我们将此捕获组命名为 placeholder。让我们看看如何使用命名的捕获组：

```java
assertThat(replaceTokens("Hi ${name} at ${company}",
  "${(?<placeholder>[A-Za-z0-9-_]+)}",
  match -> placeholderValues.get(match.group("placeholder"))))
  .isEqualTo("Hi Bill at Baeldung");
```

在这里我们可以看到，从Matcher中获取命名组的值只涉及使用名称作为输入的组 ，而不是数字。

## 六，总结

在本文中，我们研究了如何使用强大的正则表达式在我们的字符串中查找标记。我们了解了find方法如何与Matcher一起使用来向我们展示匹配项。

然后我们创建并推广了一种算法，使我们能够逐个标记地进行替换。

最后，我们研究了几个转义字符和填充模板的常见用例。