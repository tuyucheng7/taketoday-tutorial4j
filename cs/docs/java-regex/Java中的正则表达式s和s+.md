## 1. 概述

字符串替换是我们在Java中处理字符串时的标准操作。

感谢[String](https://www.baeldung.com/tag/java-string/)类中方便的[replaceAll()](https://www.baeldung.com/string/replace-all) 方法，我们可以轻松地使用[正则表达式](https://www.baeldung.com/regular-expressions-java)进行字符串替换。但是，有时表达式可能会造成混淆，例如s和s+。 

在这个简短的教程中，我们将通过示例了解两个正则表达式之间的区别。

## 2. s和s+的区别

正则表达式s是预定义的字符类。它表示单个空白字符。让我们回顾一下空白字符集：

```java
[ tnx0Bfr]
```

加号+是贪心量词，表示一次或多次。例如，表达式X+ 匹配一个或多个 X 字符。

因此，正则表达式s匹配单个空白字符，而 s+ 将匹配一个或多个空白字符。

## 3. replaceAll()非空替换

我们已经了解了正则表达式s和s+的含义。

现在，让我们看看replaceAll()方法对这两个正则表达式的行为有何不同。

我们将使用字符串作为所有示例的输入文本：

```java
String INPUT_STR = "Text   With     Whitespaces!   ";
```

让我们尝试将s作为参数传递给replaceAll()方法：

```java
String result = INPUT_STR.replaceAll("s", "_");
assertEquals("Text___With_____Whitespaces!___", result);
```

replaceAll ()方法查找单个空白字符并将每个匹配项替换为下划线。我们在输入文本中有十一个空白字符。因此，将发生十一次替换。

接下来，让我们将正则表达式s+传递 给 replaceAll()方法：

```java
String result = INPUT_STR.replaceAll("s+", "_");
assertEquals("Text_With_Whitespaces!_", result);
```

由于贪婪量词+，replaceAll()方法将匹配最长的连续空白字符序列，并用下划线替换每个匹配项。

在我们的输入文本中，我们有三个连续的空白字符序列。因此，三个中的每一个都会成为下划线。

## 4. replaceAll()使用空替换

replaceAll() 方法的另一个常见用法是从输入文本中删除匹配的模式。我们通常通过传递一个空字符串作为方法的替换来做到这一点。

让我们看看如果我们使用带有s正则表达式的replaceAll()方法删除空白字符，我们会得到什么结果：

```java
String result1 = INPUT_STR.replaceAll("s", "");
assertEquals("TextWithWhitespaces!", result1);
```

现在，我们将另一个正则表达式s+传递给replaceAll()方法：

```java
String result2 = INPUT_STR.replaceAll("s+", "");
assertEquals("TextWithWhitespaces!", result2);

```

因为替换是一个空字符串，所以两个replaceAll()调用产生相同的结果，即使两个正则表达式具有不同的含义：

```java
assertEquals(result1, result2);
```

如果我们比较两个 replaceAll()调用，带有s+ 的调用效率更高。这是因为它只用三个替换来完成工作，而用s调用将做十一个替换。

## 5.总结

在这篇简短的文章中，我们了解了正则表达式s和s+。

我们还看到了replaceAll()方法对这两个表达式的行为有何不同。