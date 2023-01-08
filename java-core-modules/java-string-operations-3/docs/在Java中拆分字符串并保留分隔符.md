## 1. 概述

程序员经常遇到涉及拆分字符串的算法。在特殊情况下，可能需要根据单个或多个不同的分隔符拆分字符串，并将分隔符作为拆分操作的一部分返回。

让我们详细讨论这个字符串拆分问题的不同可用解决方案。

## 2. 基础

Java 世界提供了相当多的库(java.lang.String、Guava 和 Apache Commons，仅举几例)来促进在简单和相当复杂的情况下[拆分字符串。](https://www.baeldung.com/java-split-string)此外，功能丰富的[正则表达式](https://www.baeldung.com/regular-expressions-java)在拆分围绕特定模式匹配的问题时提供了额外的灵活性。

## 3.环视断言

在正则表达式中，环视断言表明可以通过在源字符串的当前位置向前看 (lookahead) 或向后看 (lookbehind) 另一个模式来进行匹配。让我们通过一个例子更好地理解这一点。

前瞻断言Java(?=Baeldung) 仅当其后跟“Baeldung”时才匹配“Java ” 。

同样，否定后向断言(?<!#)d+仅在数字前面没有“#”时才匹配该数字。

让我们使用这样的环视断言正则表达式并设计一个解决我们问题的方法。

在本文解释的所有示例中，我们将使用两个简单的String：

```java
String text = "Hello@World@This@Is@A@Java@Program";
String textMixed = "@HelloWorld@This:Is@A#Java#Program";
```

## 4. 使用String.split()

让我们从使用核心Java库的String类中的split()方法开始。

此外，我们将评估适当的先行断言、后行断言以及它们的组合以根据需要拆分字符串。

### 4.1. 正面前瞻

首先，让我们使用前瞻断言“(( ?=@ ))”并围绕其匹配拆分字符串文本：

```java
String[] splits = text.split("((?=@))");
```

前瞻正则表达式通过“@”符号的前向匹配来拆分字符串。结果数组的内容是：

```java
[Hello, @World, @This, @Is, @A, @Java, @Program]
```

使用此正则表达式不会在拆分数组中单独返回分隔符。让我们尝试另一种方法。

### 4.2. 正面回顾

我们还可以使用正后向断言“((?< =@ ))”来拆分字符串文本：

```java
String[] splits = text.split("((?<=@))");
```

但是，生成的输出仍然不会包含分隔符作为数组的单个元素：

```plaintext
[Hello@, World@, This@, Is@, A@, Java@, Program]
```

### 4.3. 积极的前瞻或后视

我们可以将上述两个解释的环视与逻辑或结合使用，并在实际中看到它。

生成的正则表达式“(( ?=@ )|(?< =@ ))”肯定会给我们想要的结果。 下面的代码片段演示了这一点：

```java
String[] splits = text.split("((?=@)|(?<=@))");
```

上面的正则表达式拆分字符串，生成的数组包含分隔符：

```plaintext
[Hello, @, World, @, This, @, Is, @, A, @, Java, @, Program]
```

现在我们了解了所需的环视断言正则表达式，我们可以根据输入字符串中存在的不同类型的分隔符对其进行修改。

让我们尝试使用合适的正则表达式拆分之前定义的textMixed ：

```java
String[] splitsMixed = textMixed.split("((?=:|#|@)|(?<=:|#|@))");
```

执行上述代码行后看到以下结果也就不足为奇了：

```java
[@, HelloWorld, @, This, :, Is, @, A, #, Java, #, Program]
```

## 5. 使用 Guava Splitter

考虑到现在我们已经清楚了上一节中讨论的正则表达式断言，让我们深入研究 Google 提供的Java库。

[Guava](https://www.baeldung.com/guava-guide)的Splitter类提供[了 on()](https://guava.dev/releases/23.0/api/docs/com/google/common/base/Splitter.html#on-java.util.regex.Pattern-)和[onPattern()](https://guava.dev/releases/23.0/api/docs/com/google/common/base/Splitter.html#onPattern-java.lang.String-)方法来使用正则表达式模式作为分隔符来拆分字符串。

首先，让我们看看它们对包含单个分隔符“@”的字符串文本 的操作：

```java
List<String> splits = Splitter.onPattern("((?=@)|(?<=@))").splitToList(text);
List<String> splits2 = Splitter.on(Pattern.compile("((?=@)|(?<=@))")).splitToList(text);
```

执行上述代码行的结果与split 方法生成的结果非常相似，除了我们现在有List而不是数组。

同样，我们也可以使用这些方法来拆分包含多个不同分隔符的字符串：

```java
List<String> splitsMixed = Splitter.onPattern("((?=:|#|@)|(?<=:|#|@))").splitToList(textMixed);
List<String> splitsMixed2 = Splitter.on(Pattern.compile("((?=:|#|@)|(?<=:|#|@))")).splitToList(textMixed);
```

如我们所见，上述两种方法之间的区别非常明显。

on() 方法接受java.util.regex.Pattern的参数，而onPattern()方法只接受分隔符正则表达式作为String。

## 6. 使用 Apache Commons StringUtils

我们还可以利用[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)项目的StringUtils方法splitByCharacterType()。

请务必注意，此方法的工作原理是根据[java.lang.Character.getType(char)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#getType(char))返回的字符类型拆分输入字符串。在这里，我们无法选择或提取我们选择的分隔符。

此外，当源字符串在整个过程中具有恒定大小写(大写或小写)时，它会提供最佳结果：

```java
String[] splits = StringUtils.splitByCharacterType("pg@no;10@hello;world@this;is@a#10words;Java#Program");
```

在上面的字符串中看到的不同字符类型是大写和小写字母、数字和特殊字符(@；#)。

因此，正如预期的那样，生成的数组拆分如下所示：

```plaintext
[pg, @, no, ;, 10, @, hello, ;, world, @, this, ;, is, @, a, #, 10, words, ;, J, ava, #, P, rogram]
```

## 七、总结

在本文中，我们了解了如何以分隔符在结果数组中也可用的方式拆分字符串。

首先，我们讨论了环视断言并使用它们来获得所需的结果。后来，我们使用了Guava库提供的方法，也得到了类似的结果。

最后，我们总结了 Apache Commons Lang 库，它提供了一种更加用户友好的方法来解决拆分字符串的相关问题，同时返回分隔符。