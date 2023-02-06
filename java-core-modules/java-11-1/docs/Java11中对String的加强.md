## 1. 概述

Java 11为常用的[String类](https://www.baeldung.com/java-string)添加了一些有用的API。在本教程中，我们将探索和使用这些新的 API。

## 2. repeat()

顾名思义，repeat()实例方法重复字符串内容。

**它返回一个字符串，其值为重复n次的字符串的串联，其中n作为参数传递**：

```java
@Test
public void whenRepeatStringTwice_thenGetStringTwice() {
    String output = "La ".repeat(2) + "Land";

    is(output).equals("La La Land");
}
```

此外，如果字符串为空或计数为零，repeat()将返回一个空字符串。

## 3. strip*()

**strip()实例方法返回一个删除所有前导和尾随空格的字符串**：

```java
@Test
public void whenStripString_thenReturnStringWithoutWhitespaces() {
    is("\n\t  hello   \u2005".strip()).equals("hello");
}
```

Java 11还添加了方法stripLeading()和stripTrailing()，它们分别处理前导和尾随空格。

### 3.1 strip()和trim()之间的区别

strip*()根据Character.isWhitespace()确定字符是否为空格。换句话说，**它知道Unicode空格字符**。

这与[trim()](https://www.baeldung.com/string/trim)不同，trim()将空格定义为小于或等于Unicode空格字符(U+0020)的任何字符。如果我们在前面的例子中使用trim()，我们会得到不同的结果：

```java
@Test
public void whenTrimAdvanceString_thenReturnStringWithWhitespaces() {
    is("\n\t  hello   \u2005".trim()).equals("hello   \u2005");
}
```

请注意trim()如何能够修剪前导空格，但它没有修剪尾随空格。这是因为trim()不知道Unicode空格字符，因此不认为'\u2005'是空格字符。

## 4. isBlank()

**如果字符串为空或仅包含空格，则isBlank()实例方法返回true。否则，它返回false**：

```java
@Test
public void whenBlankString_thenReturnTrue() {
    assertTrue("\n\t\u2005  ".isBlank());
}
```

同样，isBlank()方法可以识别Unicode空格字符，就像strip()一样。

## 5. lines()

**lines()实例方法返回从字符串中提取的行**[流](https://www.baeldung.com/java-8-streams-introduction)**，由行终止符分隔：**

```java
@Test
public void whenMultilineString_thenReturnNonEmptyLineCount() {
    String multilineStr = "This is\n \n a multiline\n string.";

    long lineCount = multilineStr.lines()
      	.filter(String::isBlank)
      	.count();

    is(lineCount).equals(3L);
}
```

行终止符是以下之一：“\n”、“\r”或“\r\n”。

**流包含按照它们出现的顺序的行，从每一行中删除行终止符**。

这种方法应该优于split()，因为它提供了更好的中断多行输入的性能。

## 6. 总结

在这篇快速文章中，我们探讨了Java 11中的新字符串API。